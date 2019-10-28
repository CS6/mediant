package io.numbers.mediant.ui.main.thread_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.R
import io.numbers.mediant.ui.listeners.ItemClickListener
import io.textile.pb.Model

class ThreadListRecyclerViewAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<Model.Thread, ThreadListRecyclerViewAdapter.ViewHolder>(itemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent, itemClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val threadNameTextView: TextView = itemView.findViewById(R.id.threadName)
        private val threadIdTextView: TextView = itemView.findViewById(R.id.threadId)

        fun bind(item: Model.Thread) {
            threadNameTextView.text = item.name
            threadIdTextView.text = item.id
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(adapterPosition)

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: ItemClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_thread_item, parent, false)
                return ViewHolder(view, itemClickListener)
            }
        }
    }
}

val itemCallback = object : DiffUtil.ItemCallback<Model.Thread>() {
    override fun areItemsTheSame(oldItem: Model.Thread, newItem: Model.Thread) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Model.Thread, newItem: Model.Thread) =
        oldItem == newItem
}