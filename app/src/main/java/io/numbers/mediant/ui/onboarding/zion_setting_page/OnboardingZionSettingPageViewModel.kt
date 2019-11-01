package io.numbers.mediant.ui.onboarding.zion_setting_page

import androidx.lifecycle.*
import io.numbers.mediant.R
import io.numbers.mediant.api.zion.ZionService
import javax.inject.Inject

class OnboardingZionSettingPageViewModel @Inject constructor(
    zionService: ZionService
) : ViewModel() {

    val isZionSupported = MutableLiveData(false)
    val zionEnabled = MediatorLiveData<Boolean>()
    val switchText: LiveData<Int> = Transformations.map(zionEnabled) { zionEnabled ->
        isZionSupported.value?.also { isZionSupported ->
            if (!isZionSupported) return@map R.string.unsupported_device
            else if (zionEnabled) return@map R.string.zion_enabled
            else return@map R.string.zion_disabled
        }
        R.string.unsupported_device
    }

    init {
        zionEnabled.addSource(isZionSupported) { zionEnabled.value = isZionSupported.value }
        isZionSupported.value = zionService.isHtcExodus1
    }
}