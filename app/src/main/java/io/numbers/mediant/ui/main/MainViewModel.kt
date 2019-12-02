package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.R
import io.numbers.mediant.api.Mediant
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mediant: Mediant
) : ViewModel() {

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    fun uploadImage() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mediant.uploadImage()
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun uploadVideo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mediant.uploadVideo()
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun createImageFile(root: File): File {
        createCurrentOutputFolder(root)
        return File(mediant.currentOutputFolder, "media.jpg").apply {
            mediant.currentImagePath = absolutePath
            Timber.i("New image will be saved to: ${mediant.currentImagePath}")
        }
    }

    fun createVideoFile(root: File): File {
        createCurrentOutputFolder(root)
        return File(mediant.currentOutputFolder, "media.mp4").apply {
            mediant.currentVideoPath = absolutePath
            Timber.i("New video will be saved to: ${mediant.currentVideoPath}")
        }
    }

    private fun createCurrentOutputFolder(root: File) {
        mediant.currentOutputFolder = root.resolve("${System.currentTimeMillis()}")
        if (!mediant.currentOutputFolder.exists()) mediant.currentOutputFolder.mkdir()
    }
}