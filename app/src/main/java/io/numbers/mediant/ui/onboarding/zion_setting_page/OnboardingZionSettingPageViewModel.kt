package io.numbers.mediant.ui.onboarding.zion_setting_page

import androidx.lifecycle.*
import com.htc.htcwalletsdk.Export.RESULT
import io.numbers.mediant.R
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingZionSettingPageViewModel(
    private val zionService: ZionService,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    val isZionSupported = MutableLiveData(false)
    val zionEnabled = MutableLiveData(false)
    val switchText: LiveData<Int> = Transformations.map(zionEnabled) { zionEnabled ->
        isZionSupported.value?.also { isZionSupported ->
            if (!isZionSupported) return@map R.string.unsupported_device
            else if (zionEnabled) return@map R.string.zion_enabled
            else return@map R.string.zion_disabled
        }
        R.string.unsupported_device
    }

    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    init {
        isZionSupported.value = zionService.isSupported
        zionEnabled.value = preferenceHelper.signWithZion
    }

    fun syncSignWithZionPreference() {
        try {
            preferenceHelper.signWithZion = zionEnabled.value!!
            if (isZionSupported.value!! && zionEnabled.value!!) {
                viewModelScope.launch(Dispatchers.Default) {
                    val result = zionService.initZkma()
                    if (result != RESULT.SUCCESS) {
                        zionEnabled.postValue(false)
                        throw RuntimeException("Error when initialize ZKMA with code: $result")
                    }
                }
            }
        } catch (e: Exception) {
            showErrorSnackbar.value = Event(e)
        }
    }
}