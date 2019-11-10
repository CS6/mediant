package io.numbers.mediant.ui.listeners

import io.textile.textile.FeedItemData

interface FeedItemListener {

    fun onShowDetails(feedItemData: FeedItemData)

    fun onPublish(feedItemData: FeedItemData)

    fun onDelete(feedItemData: FeedItemData)
}