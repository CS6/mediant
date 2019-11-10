package io.numbers.mediant.ui.publishing

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

class PublishingViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService
) : ViewModel() {

    val dataHash = MutableLiveData("")
    val fileHash = MutableLiveData("")
    val imageDrawable = MediatorLiveData<Drawable>()

    val userName = MutableLiveData("")
    val date = MutableLiveData("")
    val threadList = textileService.publicThreadList
    val isLoading = MutableLiveData(false)

    init {
        loadThreadList()
        imageDrawable.addSource(fileHash) { if (!it.isNullOrEmpty()) updateFilePreview(it) }
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
    }

    private fun updateFilePreview(fileHash: String) {
        textileService.getMediantBlock(fileHash) { bytes, mediaType, _, _ ->
            when (mediaType) {
                MediaType.JPG -> updateImagePreview(bytes)
                MediaType.MP4 -> Timber.e("$mediaType not yet implemented.")
            }
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

    fun publishFile(threadId: String) {
        dataHash.value?.also { hash ->
            textileService.shareFile(hash, threadId) { Timber.i("shared: ${it.id}") }
        }
    }

}