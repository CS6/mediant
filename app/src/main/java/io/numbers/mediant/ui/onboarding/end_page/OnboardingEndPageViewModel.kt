package io.numbers.mediant.ui.onboarding.end_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.viewmodel.Event
import javax.inject.Inject

class OnboardingEndPageViewModel @Inject constructor() : ViewModel() {

    val navToMainFragmentEvent = MutableLiveData<Event<Unit>>()

    fun navToMain() {
        navToMainFragmentEvent.value = Event(Unit)
    }
}