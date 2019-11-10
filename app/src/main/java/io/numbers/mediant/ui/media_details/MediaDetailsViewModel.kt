package io.numbers.mediant.ui.media_details

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.MediaType
import io.numbers.mediant.api.textile.TextileService
import timber.log.Timber
import javax.inject.Inject

class MediaDetailsViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService
) : ViewModel() {

    val fileHash = MutableLiveData("")

    val imageDrawable = MediatorLiveData<Drawable>()

    val userName = MutableLiveData("")
    val blockTimestamp = MutableLiveData("")
    val proof = MutableLiveData("")
    val proofSignature = MutableLiveData("")
    val mediaSignature = MutableLiveData("")
    val signatureProvider = MutableLiveData("")
    val blockHash = MutableLiveData("")

    init {
        imageDrawable.addSource(fileHash) { updateMediaDetails(it) }
    }

    private fun updateMediaDetails(mediaHash: String) {
        Timber.i("update media details: $mediaHash")
        textileService.getMediantBlock(mediaHash) { bytes, mediaType, proofSignatureBundle, signatureProvider ->
            when (mediaType) {
                MediaType.JPG -> updateImagePreview(bytes)
                MediaType.MP4 -> Timber.e("$mediaType not yet implemented.")
            }
            proof.postValue(proofSignatureBundle.proofSignature)
            proofSignature.postValue(proofSignatureBundle.proofSignature)
            mediaSignature.postValue(proofSignatureBundle.mediaSignature)
            this.signatureProvider.postValue(signatureProvider.name)
        }
    }

    private fun updateImagePreview(byteArray: ByteArray) {
        imageDrawable.postValue(
            BitmapDrawable(
                application.resources,
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            )
        )
    }
}