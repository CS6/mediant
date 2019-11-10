package io.numbers.mediant.api.textile

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.numbers.mediant.BuildConfig
import io.numbers.mediant.R
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.util.PreferenceHelper
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.*
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject

private const val TEXTILE_FOLDER_NAME = "textile"
// TODO: implement infinite recycler view (via paging library) to reduce this limit
private const val REQUEST_LIMIT = 999
const val EXTERNAL_INVITE_LINK_HOST = "https://www.textile.photos/invites/new"

class TextileService @Inject constructor(
    private val textile: Textile,
    private val preferenceHelper: PreferenceHelper,
    private val application: Application
) {

    val hasLaunched = MutableLiveData(false)
    val isNodeOnline = MutableLiveData(textile.isNodeOnline)

    val threadList = MutableLiveData<List<Model.Thread>>()
    val publicThreadList: LiveData<List<Model.Thread>> = Transformations.map(threadList) { list ->
        list.filter { it.id != preferenceHelper.personalThreadId }
    }

    // The preference manager does not currently store a strong reference to the listener. We must
    // store a strong reference to the listener, or it will be susceptible to garbage collection.
    private lateinit var userNamePreferenceListener: SharedPreferences.OnSharedPreferenceChangeListener

    private val feedItemUpdateEventType: EnumSet<FeedItemType> =
        EnumSet.of(
            FeedItemType.FILES,
            FeedItemType.JOIN,
            FeedItemType.IGNORE
        )

    init {
        initNodeStatusLiveDataListeners()
        initThreadLiveDataListeners()
        initUserNamePreferenceListeners()
    }

    /**
     * Initialization
     */

    private val textilePath by lazy {
        File(
            application.applicationContext.filesDir,
            TEXTILE_FOLDER_NAME
        ).absolutePath
    }

    fun launch() {
        hasLaunched.value = true
        Textile.launch(application.applicationContext, textilePath, false)
    }

    fun hasInitialized(): Boolean = Textile.isInitialized(textilePath)

    fun createNewWalletAndAccount(): String {
        val phrase = Textile.initializeCreatingNewWalletAndAccount(textilePath, false, false)
        preferenceHelper.walletRecoveryPhrase = phrase
        Timber.i("Create new wallet: $phrase")
        return phrase
    }

    private fun initNodeStatusLiveDataListeners() {
        addEventListener(object : BaseTextileEventListener() {
            override fun nodeOnline() {
                super.nodeOnline()
                isNodeOnline.postValue(true)
            }

            override fun nodeStopped() {
                super.nodeStopped()
                isNodeOnline.postValue(false)
            }
        })
    }

    /**
     * Profile
     */

    private fun initUserNamePreferenceListeners() {
        userNamePreferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == preferenceHelper.preferenceKeyUserName) {
                textile.profile.setName(preferenceHelper.userName ?: "")
            }
        }
        preferenceHelper.sharedPreferences.registerOnSharedPreferenceChangeListener(
            userNamePreferenceListener
        )
    }

    /**
     * Thread
     */

    private fun initThreadLiveDataListeners() {
        addEventListener(object : BaseTextileEventListener() {
            override fun threadAdded(threadId: String) {
                super.threadAdded(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }

            override fun threadRemoved(threadId: String) {
                super.threadRemoved(threadId)
                threadList.postValue(textile.threads.list().itemsList)
            }

            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                // on thread renamed
                if (feedItemData.type == FeedItemType.ANNOUNCE) threadList.postValue(textile.threads.list().itemsList)
            }
        })
        safelyInvokeIfNodeOnline {
            loadThreadList()
        }
    }

    fun initPersonalThread() {
        try {
            val personalThreadId = preferenceHelper.personalThreadId
            textile.threads.get(personalThreadId)
            Timber.i("Personal thread has already been created: $personalThreadId")
        } catch (e: Exception) {
            addThread(
                textile.profile.name(),
                Model.Thread.Type.PRIVATE,
                Model.Thread.Sharing.NOT_SHARED
            ).also {
                preferenceHelper.personalThreadId = it.id
                Timber.i("Create personal thread: ${it.id}")
            }
        }
    }

    fun loadThreadList() = threadList.postValue(textile.threads.list().itemsList)

    fun addThread(
        threadName: String,
        type: Model.Thread.Type,
        sharing: Model.Thread.Sharing
    ): Model.Thread {
        val schema = View.AddThreadConfig.Schema.newBuilder()
            .setJson(readJsonSchema())
            .build()
        val config = View.AddThreadConfig.newBuilder()
            .setKey(generateThreadKey(threadName))
            .setName(threadName)
            .setType(type)
            .setSharing(sharing)
            .setSchema(schema)
            .build()
        return textile.threads.add(config)
    }

    private fun readJsonSchema() =
        application.resources
            .openRawResource(R.raw.mediant_block_schema)
            .bufferedReader()
            .use { it.readText() }

    private fun generateThreadKey(name: String): String {
        var key: String
        do {
            key =
                "${BuildConfig.APPLICATION_ID}.${BuildConfig.VERSION_NAME}.$name.${textile.profile.get().address}.${System.currentTimeMillis()}"
        } while (textile.threads.list().itemsList.any { it.key == key })
        return key
    }

    fun leaveThread(threadId: String): String = textile.threads.remove(threadId)

    fun getThread(threadId: String): Model.Thread = textile.threads.get(threadId)

    fun setThreadName(threadId: String, threadName: String) =
        textile.threads.rename(threadId, threadName)

    /**
     * Feeds
     */

    fun isFeedItemUpdateEventType(feedItemData: FeedItemData) =
        feedItemUpdateEventType.contains(feedItemData.type)

    fun listFeeds(threadId: String): List<FeedItemData> {
        Timber.i("List feeds from thread: $threadId")
        return View.FeedRequest.newBuilder()
            .setThread(threadId)
            .setLimit(REQUEST_LIMIT)
            .build()
            .let { textile.feed.list(it) }
    }

    /**
     * Files
     */

    fun addMediantBlockData(
        mediaPath: String,
        mediaType: MediaType,
        proofSignatureBundle: ProofSignatureBundle,
        signatureProvider: SignatureProvider,
        callback: Handlers.BlockHandler
    ) {
        // TODO: DANGEROUS! Memory might out of resource by reading the entire file.
        val mediaBase64 = Base64.encodeToString(File(mediaPath).readBytes(), Base64.DEFAULT)

        val data = JSONObject()
        data.put(Properties.MEDIA.value, mediaBase64)
        data.put(Properties.TYPE.value, mediaType.value)
        data.put(Properties.PROOF.value, proofSignatureBundle.proof)
        data.put(Properties.MEDIA_SIGNATURE.value, proofSignatureBundle.mediaSignature)
        data.put(Properties.PROOF_SIGNATURE.value, proofSignatureBundle.proofSignature)
        data.put(Properties.SIGNATURE_PROVIDER.value, signatureProvider.value)

        val base64 = Base64.encodeToString(data.toString().toByteArray(), Base64.DEFAULT)
        textile.files.addData(base64, preferenceHelper.personalThreadId, "", callback)
    }

    fun getMediantBlock(
        fileHash: String,
        callback: (ByteArray, MediaType, ProofSignatureBundle, SignatureProvider) -> Unit
    ) {
        textile.files.content(fileHash, object : Handlers.DataHandler {
            override fun onComplete(data: ByteArray, media: String) {
                if (media == "application/json") {
                    val jsonObject = JSONObject(String(data))

                    val proofSignatureBundle = ProofSignatureBundle(
                        jsonObject.getString(Properties.PROOF.value),
                        jsonObject.getString(Properties.PROOF_SIGNATURE.value),
                        jsonObject.getString(Properties.MEDIA_SIGNATURE.value)
                    )

                    callback(
                        Base64.decode(jsonObject.getString(Properties.MEDIA.value), Base64.DEFAULT),
                        MediaType[jsonObject.getString(Properties.TYPE.value)]!!,
                        proofSignatureBundle,
                        SignatureProvider[jsonObject.getString(Properties.SIGNATURE_PROVIDER.value)]!!
                    )
                } else Timber.e("Unknown media type: $media")
            }

            override fun onError(e: Exception) = Timber.e(e)
        })
    }

    fun getMediantBlock(
        files: View.Files,
        callback: (ByteArray, MediaType, ProofSignatureBundle, SignatureProvider) -> Unit
    ) = getMediantBlock(files.getFiles(0).file.hash, callback)

    fun shareFile(
        dataHash: String,
        threadId: String,
        callback: (Model.Block) -> Unit
    ) {
        textile.files.shareFiles(dataHash, threadId, "", object : Handlers.BlockHandler {
            override fun onComplete(block: Model.Block) = callback(block)
            override fun onError(e: java.lang.Exception) = Timber.e(e)
        })
    }

    fun ignoreFile(files: View.Files): String = textile.ignores.add(files.block)

    /**
     * Invites
     */

    fun addExternalInvite(threadId: String): String =
        addExternalInvite(textile.threads.get(threadId))

    private fun addExternalInvite(thread: Model.Thread): String {
        val invite = textile.invites.addExternal(thread.id)
        val encodedInviter = URLEncoder.encode(invite.inviter, "utf-8")
        val encodedThreadName = URLEncoder.encode(thread.name, "utf-8")
        return "$EXTERNAL_INVITE_LINK_HOST#id=${invite.id}&key=${invite.key}&inviter=${encodedInviter}&name=${encodedThreadName}"
    }

    fun acceptExternalInvite(uri: Uri) {
        val uriWithoutFragment = Uri.parse(uri.toString().replaceFirst('#', '?'))
        safelyInvokeIfNodeOnline {
            val inviteId = uriWithoutFragment.getQueryParameter("id")
            val inviteKey = uriWithoutFragment.getQueryParameter("key")
            if (inviteId.isNullOrEmpty() || inviteKey.isNullOrEmpty()) {
                throw RuntimeException("Cannot parse invite link. ID: $inviteId, Key: $inviteKey")
            } else acceptExternalInvite(inviteId, inviteKey)
        }
    }

    fun acceptExternalInvite(inviteId: String, key: String): String {
        Timber.i("Accepting external invitation: $inviteId with key $key")
        val blockHash = textile.invites.acceptExternal(inviteId, key)
        if (blockHash.isEmpty()) Timber.i("Already joined the thread.")
        else Timber.i("Invite successful: $blockHash")
        return blockHash
    }

    /**
     * Utils
     */

    // Note that the callback function will be executed on a background thread.
    fun safelyInvokeIfNodeOnline(callback: () -> Unit) {
        if (textile.isNodeOnline) callback()
        else {
            textile.addEventListener(object : BaseTextileEventListener() {
                override fun nodeOnline() {
                    super.nodeOnline()
                    callback()
                }
            })
        }
    }

    fun addEventListener(listener: TextileEventListener) = textile.addEventListener(listener)
}

val Textile.isNodeOnline: Boolean
    get() = try {
        this.online()
    } catch (e: NullPointerException) {
        false
    }