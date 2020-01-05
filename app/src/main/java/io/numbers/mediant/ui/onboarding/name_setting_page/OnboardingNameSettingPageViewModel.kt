package io.numbers.mediant.ui.onboarding.name_setting_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.util.PreferenceHelper

class OnboardingNameSettingPageViewModel(
    preferenceHelper: PreferenceHelper
) : ViewModel() {

    val userName = MutableLiveData(preferenceHelper.userName ?: "")
}