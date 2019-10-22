package io.numbers.mediant.ui.main.thread_list.thread_invite_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ThreadInviteDialogViewModel @Inject constructor() : ViewModel() {

    val inviteId = MutableLiveData("")
    val inviteKey = MutableLiveData("")
}