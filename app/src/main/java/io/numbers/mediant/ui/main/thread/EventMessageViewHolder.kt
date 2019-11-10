package io.numbers.mediant.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.numbers.mediant.R
import io.numbers.mediant.util.timestampToString
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType

class EventMessageViewHolder(itemView: View) : ThreadRecyclerViewAdapter.ViewHolder(itemView) {

    private val eventMessageTextView: TextView = itemView.findViewById(R.id.eventMessage)
    private val userNameTextView: TextView = itemView.findViewById(R.id.userName)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)

    override fun bind(item: FeedItemData) {
        try {
            eventMessageTextView.text = item.type.name
            when (item.type) {
                FeedItemType.JOIN -> {
                    userNameTextView.text = item.join.user.name
                    dateTextView.text = timestampToString(item.join.date)
                }
                FeedItemType.TEXT -> {
                    userNameTextView.text = item.text.user.name
                    dateTextView.text = timestampToString(item.text.date)
                }
                FeedItemType.COMMENT -> {
                    userNameTextView.text = item.comment.user.name
                    dateTextView.text = timestampToString(item.comment.date)
                }
                FeedItemType.LIKE -> {
                    userNameTextView.text = item.like.user.name
                    dateTextView.text = timestampToString(item.like.date)
                }
                FeedItemType.IGNORE -> {
                    userNameTextView.text = item.ignore.user.name
                    dateTextView.text = timestampToString(item.ignore.date)
                }
                FeedItemType.LEAVE -> {
                    userNameTextView.text = item.leave.user.name
                    dateTextView.text = timestampToString(item.leave.date)
                }
                FeedItemType.ANNOUNCE -> {
                    userNameTextView.text = item.announce.user.name
                    dateTextView.text = timestampToString(item.announce.date)
                }
                FeedItemType.FILES -> {
                    userNameTextView.text = item.files.user.name
                    dateTextView.text = timestampToString(item.files.date)
                }
                null -> {
                }
            }
        } catch (e: Exception) {
            eventMessageTextView.text = e.message ?: "Unknown Exception"
            userNameTextView.text = "--"
            dateTextView.text = "--"
        }
    }

    companion object {
        fun from(parent: ViewGroup): ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.layout_thread_event_message, parent, false)
            return EventMessageViewHolder(view)
        }
    }
}