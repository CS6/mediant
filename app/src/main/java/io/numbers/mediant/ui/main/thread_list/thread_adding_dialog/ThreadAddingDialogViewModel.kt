package io.numbers.mediant.ui.main.thread_list.thread_adding_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.viewmodel.Event

class ThreadAddingDialogViewModel : ViewModel() {

    val createThreadEvent = MutableLiveData<Event<Unit>>()
    val acceptInviteEvent = MutableLiveData<Event<Unit>>()

    fun createThread() {
        createThreadEvent.value = Event(Unit)
    }

    fun acceptInvite() {
        acceptInviteEvent.value = Event(Unit)
    }
}