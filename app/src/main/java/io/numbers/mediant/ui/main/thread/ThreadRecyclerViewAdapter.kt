package io.numbers.mediant.ui.main.thread

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.textile.hasSameContentsTo
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ThreadRecyclerViewAdapter(
    private val textileService: TextileService,
    private val listener: FeedItemListener,
    private val isPersonal: Boolean
) : ListAdapter<FeedItemData, ThreadRecyclerViewAdapter.ViewHolder>(itemCallback) {

    private val feedItemTypeValues = FeedItemType.values() // cache the types

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    @ExperimentalCoroutinesApi
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (feedItemTypeValues[viewType]) {
            FeedItemType.FILES -> ImageCardViewHolder.from(
                parent, textileService, listener, isPersonal
            )
            else -> EventMessageViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: FeedItemData)
    }
}

val itemCallback = object : DiffUtil.ItemCallback<FeedItemData>() {
    override fun areItemsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
        oldItem.block == newItem.block

    override fun areContentsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
        oldItem.hasSameContentsTo(newItem)
}