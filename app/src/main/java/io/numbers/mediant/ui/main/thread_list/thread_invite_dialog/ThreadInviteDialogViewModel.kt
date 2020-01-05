package io.numbers.mediant.ui.main.thread_list.thread_invite_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThreadInviteDialogViewModel : ViewModel() {

    val inviteId = MutableLiveData("")
    val inviteKey = MutableLiveData("")
}