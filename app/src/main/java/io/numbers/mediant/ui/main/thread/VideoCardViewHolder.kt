package io.numbers.mediant.ui.main.thread

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import android.widget.TextView
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.numbers.mediant.util.rescaleBitmap
import io.numbers.mediant.util.timestampToString
import io.textile.textile.FeedItemData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VideoCardViewHolder(
    itemView: View,
    private val textileService: TextileService,
    private val listener: FeedItemListener,
    private val isPersonal: Boolean
) : ThreadRecyclerViewAdapter.ViewHolder(itemView) {

    private var job: Job? = null

    //private val videoView: VideoView = itemView.findViewById(R.id.video)
    private val userNameTextView: TextView = itemView.findViewById(R.id.userName)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)
    private val showProofButton: Button = itemView.findViewById(R.id.showProofButton)
    private val publishButton: Button = itemView.findViewById(R.id.publishButton)
    private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

    override fun bind(item: FeedItemData) {
        job?.cancel()

        userNameTextView.text = item.files.user.name
        dateTextView.text = timestampToString(item.files.date)
        showProofButton.setOnClickListener { listener.onShowProof(item) }
        publishButton.setOnClickListener { listener.onPublish(item) }
        deleteButton.setOnClickListener { listener.onDelete(item) }

        /*
        textileService.fetchRawContent(item.files) {
            val video = rescaleBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            job = CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
                videoView.setImageBitmap(bitmap)
            }
        }
         */

        if (isPersonal) {
            publishButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        } else {
            publishButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            textileService: TextileService,
            listener: FeedItemListener,
            isPersonal: Boolean
        ): ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.item_thread_video_card, parent, false)
            return VideoCardViewHolder(view, textileService, listener, isPersonal)
        }
    }
}