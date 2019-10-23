package io.numbers.mediant.ui.main.thread.thread_information

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import timber.log.Timber
import javax.inject.Inject

class ThreadInformationViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    val threadId = MutableLiveData<String>()
    val threadName = MediatorLiveData<String>()
    val blockCount = MediatorLiveData<Int>()

    init {
        threadName.addSource(threadId) {
            it?.let { threadName.value = textileService.getThread(it).name }
        }
        blockCount.addSource(threadId) {
            it?.let { blockCount.value = textileService.getThread(it).blockCount }
        }
    }

    fun changeName() = Timber.d("change name")

    fun inviteOthers() = Timber.d("invite others")

    fun leaveThread() = Timber.d("leave thread")
}