package io.numbers.mediant.ui.onboarding.name_setting_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.util.PreferenceHelper
import javax.inject.Inject

class OnboardingNameSettingPageViewModel @Inject constructor(
    preferenceHelper: PreferenceHelper
) : ViewModel() {

    val userName = MutableLiveData(preferenceHelper.userName ?: "")
}