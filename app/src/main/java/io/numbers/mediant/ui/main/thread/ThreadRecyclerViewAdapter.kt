package io.numbers.mediant.ui.main.thread

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.JsonAdapter
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.textile.hasSameContentsTo
import io.numbers.mediant.model.Meta
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType

class ThreadRecyclerViewAdapter(
    private val textileService: TextileService,
    private val metaJsonAdapter: JsonAdapter<Meta>,
    private val listener: FeedItemListener,
    private val isPersonal: Boolean
) : ListAdapter<FeedItemData, ThreadRecyclerViewAdapter.ViewHolder>(itemCallback) {

    override fun getItemViewType(position: Int): Int {
        val feedItemData = getItem(position)
        return when (feedItemData.type) {
            FeedItemType.FILES -> {
                when (metaJsonAdapter.fromJson(feedItemData.files.caption)?.mediaType) {
                    Meta.MediaType.JPG -> ItemViewType.IMAGE.value
                    Meta.MediaType.MP4 -> ItemViewType.VIDEO.value
                    else -> ItemViewType.UNSUPPORTED.value
                }
            }
            else -> ItemViewType.UNSUPPORTED.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (ItemViewType[viewType]) {
            ItemViewType.IMAGE -> ImageCardViewHolder.from(
                parent, textileService, listener, isPersonal
            )
            ItemViewType.VIDEO -> VideoCardViewHolder.from(
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

    enum class ItemViewType(val value: Int) {
        IMAGE(0),
        VIDEO(1),
        UNSUPPORTED(-1);

        companion object {
            private val map = values().associateBy(ItemViewType::value)
            operator fun get(value: Int) = map[value]
        }
    }
}

val itemCallback = object : DiffUtil.ItemCallback<FeedItemData>() {
    override fun areItemsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
        oldItem.block == newItem.block

    override fun areContentsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
        oldItem.hasSameContentsTo(newItem)
}