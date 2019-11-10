package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.JsonAdapter
import io.numbers.mediant.R
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.getHashFromString
import io.numbers.mediant.viewmodel.Event
import io.textile.pb.Model
import io.textile.textile.Handlers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.witness.proofmode.crypto.HashUtils
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val textileService: TextileService,
    private val proofModeService: ProofModeService,
    private val zionService: ZionService,
    private val preferenceHelper: PreferenceHelper,
    private val metaJsonAdapter: JsonAdapter<Meta>
) : ViewModel() {

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()
    private lateinit var currentImagePath: String
    private lateinit var currentVideoPath: String

    fun uploadImage() = viewModelScope.launch(Dispatchers.IO) {
        generateMetaJson(currentImagePath, Meta.MediaType.JPG)?.also { metaJson ->
            textileService.addFile(currentImagePath, metaJson, object : Handlers.BlockHandler {
                override fun onComplete(block: Model.Block?) = showSnackbar.postValue(
                    Event(SnackbarArgs(R.string.message_media_uploaded))
                )

                override fun onError(e: Exception) = showErrorSnackbar.postValue(Event(e))
            })
        }
    }

    fun uploadVideo() = viewModelScope.launch(Dispatchers.IO) {
        generateMetaJson(currentVideoPath, Meta.MediaType.MP4)?.also { metaJson ->
            Timber.i(metaJson)
            textileService.addFile(currentVideoPath, metaJson, object : Handlers.BlockHandler {
                override fun onComplete(block: Model.Block?) = showSnackbar.postValue(
                    Event(SnackbarArgs(R.string.message_media_uploaded))
                )

                override fun onError(e: Exception) = showErrorSnackbar.postValue(Event(e))
            })
        }
    }

    private fun generateMetaJson(filePath: String, mediaType: Meta.MediaType): String? {
        val snackbarArgs =
            SnackbarArgs(R.string.message_proof_generating, Snackbar.LENGTH_INDEFINITE)
        showSnackbar.postValue(Event(snackbarArgs))
        return try {
            val proofSignatureBundle = if (preferenceHelper.signWithZion) {
                generateProofWithZion(filePath)
            } else proofModeService.generateProofAndSignatures(filePath)

            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_proof_generated)))

            metaJsonAdapter.toJson(Meta(mediaType, proofSignatureBundle))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
            null
        }
    }

    private fun generateProofWithZion(filePath: String): ProofSignatureBundle {
        val proof = proofModeService.generateProofAndSignatures(filePath).proof
        val mediaHash = HashUtils.getSHA256FromFileContent(File(filePath))
        val proofHash = getHashFromString(proof)
        return ProofSignatureBundle(
            proof,
            zionService.signMessage(proofHash),
            zionService.signMessage(mediaHash),
            Meta.SignatureProvider.ZION
        )
    }

    fun createImageFile(directory: File): File =
        File.createTempFile("JPEG_${System.currentTimeMillis()}", ".jpg", directory).apply {
            currentImagePath = absolutePath
        }

    fun createVideoFile(directory: File): File =
        File.createTempFile("MP4_${System.currentTimeMillis()}", ".mp4", directory).apply {
            currentVideoPath = absolutePath
        }
}