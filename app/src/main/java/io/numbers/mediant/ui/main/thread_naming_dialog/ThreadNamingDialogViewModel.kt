package io.numbers.mediant.ui.main.thread_naming_dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ThreadNamingDialogViewModel @Inject constructor() : ViewModel() {

    val threadName = MutableLiveData("")
}