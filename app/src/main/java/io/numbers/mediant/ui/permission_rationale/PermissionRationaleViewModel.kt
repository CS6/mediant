package io.numbers.mediant.ui.permission_rationale

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.viewmodel.Event

class PermissionRationaleViewModel : ViewModel() {

    val rationale = MutableLiveData<Int>()
    val openAppSettingsEvent = MutableLiveData<Event<Unit>>()

    fun openAppSettings() {
        openAppSettingsEvent.value = Event(Unit)
    }
}