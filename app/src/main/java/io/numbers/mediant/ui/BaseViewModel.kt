package io.numbers.mediant.ui

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.R
import io.numbers.mediant.api.canon_camera_control.ADDED_CONTENTS
import io.numbers.mediant.api.canon_camera_control.CanonCameraControlService
import io.numbers.mediant.api.textile.EXTERNAL_INVITE_LINK_HOST
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val textileService: TextileService,
    private val preferenceHelper: PreferenceHelper,
    private val canonCameraControlService: CanonCameraControlService
) : ViewModel() {

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    private var canonCameraPollingLoop: Job? = null

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
        canonCameraPollingLoop = viewModelScope.launch(Dispatchers.IO) {
            try {
                canonCameraControlService.startPolling().collect {
                    if (it.has(ADDED_CONTENTS)) {
                        val urls = it.getJSONArray(ADDED_CONTENTS)
                        for (i in 0 until urls.length()) {
                            val content = canonCameraControlService.getContent(urls.getString(i))
                        }
                    }
                }
            } catch (e: Exception) {
                preferenceHelper.enablePollingCanonCameraStatus = false
                showErrorSnackbar.postValue(Event(e))
            }
        }
    }

    fun stopCanonCameraPollingLoop() {
        canonCameraPollingLoop?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        preferenceHelper.enablePollingCanonCameraStatus = false
    }
}