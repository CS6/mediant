package io.numbers.mediant.ui

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.R
import io.numbers.mediant.api.MediantService
import io.numbers.mediant.api.canon_camera_control.ADDED_CONTENTS
import io.numbers.mediant.api.canon_camera_control.CanonCameraControlService
import io.numbers.mediant.api.session_based_signature.SessionBasedSignatureService
import io.numbers.mediant.api.textile.EXTERNAL_INVITE_LINK_HOST
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.fromInputStream
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val textileService: TextileService,
    private val preferenceHelper: PreferenceHelper,
    private val canonCameraControlService: CanonCameraControlService,
    private val sessionBasedSignatureService: SessionBasedSignatureService,
    private val mediantService: MediantService
) : ViewModel() {

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    val saveMediaFromStream = MutableLiveData<Event<InputStream>>()

    private var canonCameraPollingLoop: Job? = null

    init {
        // Disable Canon camera polling (preference) every time the app starts.
        preferenceHelper.enablePollingCanonCameraStatus = false
    }

    fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.also {
                if (it.toString().startsWith(EXTERNAL_INVITE_LINK_HOST)) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            textileService.acceptExternalInvite(it)
                            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_accepting_thread_invite)))
                        } catch (e: Exception) {
                            showErrorSnackbar.postValue(Event(e))
                        }
                    }
                } else showSnackbar.postValue(Event(SnackbarArgs(R.string.message_invite_parsing_error)))
            }
        }
    }

    fun startCanonCameraPollingLoop() {
        Timber.i("Start polling loop.")
        canonCameraPollingLoop = viewModelScope.launch(Dispatchers.IO) {
            try {
                canonCameraControlService.startPolling().collect {
                    Timber.v(it.toString(2))
                    if (it.has(ADDED_CONTENTS)) {
                        val urls = it.getJSONArray(ADDED_CONTENTS)
                        for (i in 0 until urls.length()) {
                            val url = urls.getString(i)
                            Timber.i("Detect new contents: $url")
                            if (url.endsWith(".jpg", true)) {
                                val content = canonCameraControlService.getContent(url)
                                Timber.d("${content.hashCode()}")
                                saveMediaFromStream.postValue(Event(content.byteStream()))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                preferenceHelper.enablePollingCanonCameraStatus = false
                showErrorSnackbar.postValue(Event(e))
            }
        }
    }

    fun uploadImage(
        directory: File,
        inputStream: InputStream
    ) = viewModelScope.launch(Dispatchers.IO) {
        Timber.d("Try to upload image from $directory.")
        try {
            val mediaFile = mediantService.createMediaFile(directory, "media.jpg")
            mediaFile.fromInputStream(inputStream)
            if (!sessionBasedSignatureService.checkSessionStatus()) {
                showSnackbar.postValue(Event(SnackbarArgs(R.string.message_wait_session_creation)))
            }
            mediantService.uploadImageByRestful(mediaFile)
            mediantService.uploadImage(mediaFile, directory)
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_media_uploaded)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }

    fun stopCanonCameraPollingLoop() {
        Timber.i("Stop polling loop.")
        canonCameraPollingLoop?.cancel()
    }
}