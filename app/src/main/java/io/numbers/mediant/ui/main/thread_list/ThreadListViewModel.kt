package io.numbers.mediant.ui.main.thread_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.TextileService
import io.textile.pb.Model
import javax.inject.Inject

class ThreadListViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    val threadList = textileService.threadList
    val isLoading = MutableLiveData(false)

    init {
        loadThreadList()
    }

    fun loadThreadList() {
        isLoading.value = true
        textileService.loadThreadList()
        isLoading.value = false
    }

    fun addThread() = textileService.addThread()

    fun leaveThread(thread: Model.Thread) = textileService.leaveThread(thread)
}