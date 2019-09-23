package io.dt42.mediant.wrappers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.dt42.mediant.BuildConfig
import io.dt42.mediant.activities.TAG
import io.textile.pb.Model
import io.textile.pb.Model.Thread.Sharing
import io.textile.pb.Model.Thread.Type
import io.textile.pb.View
import io.textile.textile.*
import java.io.File

private const val PREFERENCE_KEY_PUBLIC_THREAD_ID = "PREFERENCE_KEY_PUBLIC_THREAD_ID"
private const val PREFERENCE_KEY_PERSONAL_THREAD_ID = "PREFERENCE_KEY_PERSONAL_THREAD_ID"
private const val REQUEST_LIMIT = 999

// TODO: DEVELOPMENT ONLY (we should replace it with private AWS cafe node)
//   Remember to remove the private AWS cafe token if we want to open source this project.
private const val DEV_CAFE_URL = "https://us-west-dev.textile.cafe"
private const val DEV_CAFE_TOKEN = "uggU4NcVGFSPchULpa2zG2NRjw2bFzaiJo3BYAgaFyzCUPRLuAgToE3HXPyo"

object TextileWrapper {

    private lateinit var pref: SharedPreferences
    private lateinit var prefChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val onPublicThreadIdChangeListeners = mutableListOf<() -> Unit>()
    private val onPersonalThreadIdChangeListeners = mutableListOf<() -> Unit>()
    var publicThreadId: String?
        get() = pref.getString(PREFERENCE_KEY_PUBLIC_THREAD_ID, null)
        set(value) = pref.edit().putString(PREFERENCE_KEY_PUBLIC_THREAD_ID, value).apply()
    var personalThreadId: String?
        get() = pref.getString(PREFERENCE_KEY_PERSONAL_THREAD_ID, null)
        set(value) = pref.edit().putString(PREFERENCE_KEY_PERSONAL_THREAD_ID, value).apply()

    private val isOnline: Boolean
        get() = try {
            Textile.instance().online()
        } catch (e: NullPointerException) {
            false
        }

    fun init(context: Context, debug: Boolean) {
        initializeSharedPreferences(context)
        val path = File(context.filesDir, "textile-go").absolutePath
        if (!Textile.isInitialized(path)) {
            val phrase = Textile.initializeCreatingNewWalletAndAccount(path, debug, false)
            Log.i(TAG, "Create new wallet: $phrase")
        }
        Textile.launch(context, path, debug)
        Textile.instance().addEventListener(TextileLoggingListener())
        invokeWhenNodeOnline {
            addCafe(DEV_CAFE_URL, DEV_CAFE_TOKEN)
            initPersonalThread()
        }
    }

    private fun initializeSharedPreferences(context: Context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context)
        prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            if (pref == this.pref) {
                when (key) {
                    PREFERENCE_KEY_PUBLIC_THREAD_ID -> onPublicThreadIdChangeListeners.forEach { it() }
                    PREFERENCE_KEY_PERSONAL_THREAD_ID -> onPersonalThreadIdChangeListeners.forEach { it() }
                }
            }
        }
        pref.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    /*-------------------------------------------
     * Threads
     *-----------------------------------------*/
    fun logThreads() {
        val threadList = Textile.instance().threads.list()
        for (i in 0 until threadList.itemsCount) {
            Log.i(TAG, "${threadList.getItems(i).id}: ${threadList.getItems(i).blockCount}")
        }
    }

    private fun initPersonalThread() {
        val profileAddress = Textile.instance().profile.get().address
        try {
            Textile.instance().threads.get(personalThreadId!!)
        } catch (e: Exception) {
            createThread(profileAddress, Type.PRIVATE, Sharing.NOT_SHARED).apply {
                personalThreadId = id
                Log.i(TAG, "Create personal thread: $name ($id)")
            }
        } finally {
            Log.i(TAG, "Personal thread has been created: $profileAddress ($personalThreadId)")
        }
    }

    private fun createThread(name: String, type: Type, sharing: Sharing): Model.Thread {
        val schema = View.AddThreadConfig.Schema.newBuilder()
            .setPreset(View.AddThreadConfig.Schema.Preset.MEDIA)
            .build()
        val config = View.AddThreadConfig.newBuilder()
            .setKey("${BuildConfig.APPLICATION_ID}.${BuildConfig.VERSION_NAME}.$name")
            .setName(name)
            .setType(type)
            .setSharing(sharing)
            .setSchema(schema)
            .build()
        return Textile.instance().threads.add(config)
    }

    fun removeThread(threadId: String) {
        Textile.instance().threads.remove(threadId)
    }

    private fun findParentThread(blockId: String): Model.Thread {
        // TODO: We should use block API instead of feed API after block API has been implemented.
        // https://github.com/textileio/android-textile/issues/15
        val threadList = Textile.instance().threads.list()
        for (i in 0 until threadList.itemsCount) {
            val threadItem = threadList.getItems(i)
            listFeeds(threadItem.id).forEach {
                if (it.block == blockId) {
                    return threadItem
                }
            }
        }
        throw NoSuchElementException("Cannot find the block ($blockId) via feed API.")
    }

    /*-------------------------------------------
     * Feeds
     *-----------------------------------------*/

    fun listFeeds(threadId: String): ArrayList<FeedItemData> {
        val request = View.FeedRequest.newBuilder()
            .setThread(threadId)
            .setLimit(REQUEST_LIMIT)
            .build()
        return Textile.instance().feed.list(request)
    }

    /*-------------------------------------------
     * Files
     *-----------------------------------------*/

    // TODO: wrap this function with suspendCoroutine for async return
    fun addFile(filePath: String, threadId: String, caption: String) =
        Textile.instance().files.addFiles(filePath, threadId, caption,
            object : Handlers.BlockHandler {
                override fun onComplete(block: Model.Block) {
                    Log.i(TAG, "Add file ($filePath) to thread (${block.thread}) successfully.")
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, "Add file ($filePath) to thread ($threadId) with error.")
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            })

    // TODO: wrap this function with suspendCoroutine for async return
    fun getImageContent(path: String, minWidth: Long, handler: Handlers.DataHandler) {
        // imageContentForMinWidth usage: (Textile has not documented)
        // https://github.com/textileio/photos/blob/master/App/Components/authoring-input.tsx#L184
        Textile.instance().files.imageContentForMinWidth(path, minWidth, handler)
    }

    /*-------------------------------------------
     * Invites
     *-----------------------------------------*/

    /**
     * Accept invitation sent from Textile Photo
     */
    fun acceptExternalInvitation(inviteId: String, key: String): Model.Thread {
        Log.i(TAG, "Accepting invitation: $inviteId with key $key")
        val newBlockHash = Textile.instance().invites.acceptExternal(inviteId, key)
        return findParentThread(newBlockHash).apply {
            Log.i(TAG, "Join to thread: $id")
        }
    }

    /*-------------------------------------------
     * Cafes
     *-----------------------------------------*/
    private fun addCafe(url: String, token: String) {
        Textile.instance().cafes.register(
            url, token,
            object : Handlers.ErrorHandler {
                override fun onComplete() {
                    Log.i(TAG, "Add Cafe ($url) successfully.")
                }

                override fun onError(e: Exception?) {
                    Log.e(TAG, "Add Cafe with error: " + Log.getStackTraceString(e))
                }
            })
    }

    /*-------------------------------------------
     * Utils
     *-----------------------------------------*/

    /**
     * Invoke the callback function after node has online. If the node has already online, the
     *   callback will be invoked immediately.
     * @param callback the callback function
     */
    fun invokeWhenNodeOnline(callback: () -> Unit) {
        if (isOnline) {
            callback.invoke()
        } else {
            Textile.instance().addEventListener(object : BaseTextileEventListener() {
                override fun nodeOnline() {
                    super.nodeOnline()
                    callback.invoke()
                }
            })
        }
    }

    fun addOnPersonalThreadUpdateReceivedListener(callback: (FeedItemData) -> Unit) {
        Textile.instance().addEventListener(object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                if (threadId == personalThreadId) {
                    callback.invoke(feedItemData)
                }
            }
        })
    }

    fun addOnPublicThreadUpdateReceivedListener(callback: (FeedItemData) -> Unit) {
        Textile.instance().addEventListener(object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                if (threadId == publicThreadId) {
                    callback.invoke(feedItemData)
                }
            }
        })
    }

    fun addOnPersonalThreadIdChangedListener(callback: () -> Unit) {
        onPersonalThreadIdChangeListeners.add(callback)
    }

    fun addOnPublicThreadIdChangedListener(callback: () -> Unit) {
        onPublicThreadIdChangeListeners.add(callback)
    }
}