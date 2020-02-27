package io.numbers.mediant.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import androidx.camera.core.Preview
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.numbers.mediant.R
import io.numbers.mediant.api.MediantService
import io.numbers.mediant.api.canon_camera_control.CanonCameraControlService
import io.numbers.mediant.api.dual_capture.DualCaptureService
import io.numbers.mediant.api.session_based_signature.SessionBasedSignatureService
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mediantService: MediantService,
    private val sessionBasedSignatureService: SessionBasedSignatureService,
    private val canonCameraControlService: CanonCameraControlService,
    private val dualCaptureService: DualCaptureService
) : ViewModel() {

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    private lateinit var mediaFile: File
    private lateinit var currentOutputFolder: File

    val liveViewCardState = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val currentLiveView = MutableLiveData<Bitmap>()
    private var liveViewJob: Job? = null

    fun uploadImage() = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (!sessionBasedSignatureService.checkSessionStatus()) {
                showSnackbar.postValue(Event(SnackbarArgs(R.string.message_wait_session_creation)))
            }
            mediantService.uploadImage(mediaFile, currentOutputFolder)
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            Timber.i("Exception $e")
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun uploadVideo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mediantService.uploadVideo(mediaFile, currentOutputFolder)
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun createMediaFile(root: File, fileName: String): File {
        currentOutputFolder = root
        mediaFile = mediantService.createMediaFile(root, fileName)
        return mediaFile
    }

    fun startLiveView() {
        liveViewJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                canonCameraControlService.startLiveView().collect {
                    val bitmap = BitmapFactory.decodeStream(it)
                    currentLiveView.postValue(bitmap)
                }
            } catch (e: Exception) {
                showErrorSnackbar.postValue(Event(e))
            }
        }
    }

    fun stopLiveView() {
        liveViewJob?.cancel()
    }

    fun getCameraPreview(): Preview {
        return dualCaptureService.getPreview()
    }
}