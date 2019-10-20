package io.numbers.mediant.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import io.numbers.mediant.R
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.SnackbarArgs
import io.numbers.mediant.util.getHashFromString
import io.numbers.mediant.viewmodel.Event
import io.textile.pb.Model
import io.textile.textile.Handlers
import kotlinx.coroutines.*
import org.witness.proofmode.crypto.HashUtils
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService,
    private val proofModeService: ProofModeService,
    private val zionService: ZionService,
    private val preferenceHelper: PreferenceHelper
) : ViewModel(), CoroutineScope by MainScope() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    private lateinit var currentPhotoPath: String

    fun uploadPhoto() = launch(Dispatchers.IO) {
        generateProofBundleJson()?.also {
            textileService.addFile(currentPhotoPath, it, object : Handlers.BlockHandler {
                override fun onComplete(block: Model.Block?) = showSnackbar.postValue(
                    Event(SnackbarArgs(application.resources.getString(R.string.message_media_uploaded)))
                )

                override fun onError(e: Exception) = showSnackbar.postValue(Event(SnackbarArgs(e)))
            })
        }
    }

    private fun generateProofBundleJson(): String? {
        val snackbarArgs = SnackbarArgs(
            application.resources.getString(R.string.message_proof_generating),
            Snackbar.LENGTH_INDEFINITE
        )
        showSnackbar.postValue(Event(snackbarArgs))
        return try {
            val proofSignatureBundle = if (preferenceHelper.signWithZion) {
                generateProofWithZion(currentPhotoPath)
            } else proofModeService.generateProofAndSignatures(currentPhotoPath)
            val proofSignatureBundleJson = Gson().toJson(proofSignatureBundle)
            showSnackbar.postValue(
                Event(SnackbarArgs(application.resources.getString(R.string.message_proof_generated)))
            )
            proofSignatureBundleJson
        } catch (e: Exception) {
            showSnackbar.postValue(Event(SnackbarArgs(e)))
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
            zionService.signMessage(mediaHash)
        )
    }

    fun createPhotoFile(directory: File): File =
        File.createTempFile("JPEG_${System.currentTimeMillis()}", ".jpg", directory).apply {
            currentPhotoPath = absolutePath
        }

    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}