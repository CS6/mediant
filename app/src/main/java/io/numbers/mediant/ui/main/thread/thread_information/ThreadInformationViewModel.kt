package io.numbers.mediant.ui.main.thread.thread_information

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.textile.pb.Model
import timber.log.Timber
import javax.inject.Inject

class ThreadInformationViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    val threadId = MutableLiveData<String>()
    val threadName = MediatorLiveData<String>()
    val threadKey = MediatorLiveData<String>()
    val schema = MediatorLiveData<String>()
    val type = MediatorLiveData<Model.Thread.Type>()
    val sharingMode = MediatorLiveData<Model.Thread.Sharing>()
    val blockCount = MediatorLiveData<Int>()
    val headBlockCount = MediatorLiveData<Int>()
    val peerCount = MediatorLiveData<Int>()

    init {
        threadName.addSource(threadId) {
            it?.let { threadName.value = textileService.getThread(it).name }
        }
        threadKey.addSource(threadId) {
            it?.let { threadKey.value = textileService.getThread(it).key }
        }
        schema.addSource(threadId) {
            it?.let { schema.value = textileService.getThread(it).schema }
        }
        type.addSource(threadId) {
            it?.let { type.value = textileService.getThread(it).type }
        }
        sharingMode.addSource(threadId) {
            it?.let { sharingMode.value = textileService.getThread(it).sharing }
        }
        blockCount.addSource(threadId) {
            it?.let { blockCount.value = textileService.getThread(it).blockCount }
        }
        headBlockCount.addSource(threadId) {
            it?.let { headBlockCount.value = textileService.getThread(it).headBlocksCount }
        }
        peerCount.addSource(threadId) {
            it?.let { peerCount.value = textileService.getThread(it).peerCount }
        }
    }

    fun changeName() = Timber.d("change name")
}