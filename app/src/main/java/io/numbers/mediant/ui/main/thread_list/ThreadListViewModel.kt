package io.numbers.mediant.ui.main.thread_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.ui.snackbar.SnackbarArgs
import io.numbers.mediant.viewmodel.Event
import io.textile.pb.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ThreadListViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    val threadList = textileService.publicThreadList
    val isLoading = MutableLiveData(false)
    val openDialog = MutableLiveData<Event<Unit>>()
    val showSnackbar = MutableLiveData<Event<SnackbarArgs>>()
    val showErrorSnackbar = MutableLiveData<Event<Exception>>()

    init {
        loadThreadList()
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
    }

    fun createThread() {
        openDialog.value = Event(Unit)
    }

    fun addThread(name: String) =
        textileService.addThread(name, Model.Thread.Type.OPEN, Model.Thread.Sharing.SHARED)

    fun acceptInvite(inviteId: String, inviteKey: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            textileService.acceptExternalInvite(inviteId, inviteKey)
            showSnackbar.postValue(Event(SnackbarArgs(R.string.message_accepting_thread_invite)))
        } catch (e: Exception) {
            showErrorSnackbar.postValue(Event(e))
        }
    }
}