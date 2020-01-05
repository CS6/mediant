package io.numbers.mediant.ui.main.thread

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.util.PreferenceHelper
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType

class ThreadViewModel(
    private val textileService: TextileService,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    var threadId = ""
        set(value) {
            field = value
            initThreadNameLiveData()
            initFeedListLiveData()
        }

    val isPersonal: Boolean
        get() = preferenceHelper.personalThreadId == threadId
    val feedList = MutableLiveData<List<FeedItemData>>()
    val isLoading = MutableLiveData(false)
    val threadName = MutableLiveData("")

    private fun initThreadNameLiveData() {
        threadName.postValue(textileService.getThread(threadId).name)
        textileService.addEventListener(object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                if (this@ThreadViewModel.threadId == threadId && feedItemData.type == FeedItemType.ANNOUNCE) {
                    threadName.postValue(textileService.getThread(threadId).name)
                }
            }
        })
    }

    private fun initFeedListLiveData() {
        loadFeedList()
        textileService.addEventListener(object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                if (threadId == this@ThreadViewModel.threadId
                    && textileService.isFeedItemUpdateEventType(feedItemData)
                ) {
                    feedList.postValue(textileService.listFeeds(threadId))
                }
            }
        })
    }

    fun loadFeedList() {
        isLoading.value = true
        feedList.value = textileService.listFeeds(threadId)
        isLoading.value = false
    }

    fun deleteFile(files: View.Files) = textileService.ignoreFile(files)
}