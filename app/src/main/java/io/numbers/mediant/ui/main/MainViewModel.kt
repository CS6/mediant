package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.R
import io.numbers.mediant.api.MediantService
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mediantService: MediantService
) : ViewModel() {

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    fun uploadImage() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mediantService.uploadImage()
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun uploadVideo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mediantService.uploadVideo()
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun createMediaFile(root: File, fileName: String) =
        mediantService.createMediaFile(root, fileName)
}