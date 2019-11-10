package io.numbers.mediant.ui.publishing

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.util.rescaleBitmap
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
    val blockTimestamp = MutableLiveData("")
    val fileMeta = MutableLiveData("")
    val threadList = textileService.publicThreadList
    val isLoading = MutableLiveData(false)

    init {
        loadThreadList()
        imageDrawable.addSource(fileHash) { if (!dataHash.value.isNullOrEmpty()) updateImage(it) }
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
    }

    private fun updateImage(fileHash: String) {
        textileService.fetchRawContent(fileHash) {
            val bitmap = rescaleBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            imageDrawable.postValue(
                BitmapDrawable(application.resources, bitmap)
            )
        }
    }

    fun publishFile(threadId: String) {
        dataHash.value?.also { hash ->
            textileService.shareFile(hash, fileMeta.value ?: "", threadId) {
                Timber.i("Shared: ${it.id}")
            }
        }
    }

}