package io.numbers.mediant.ui.main.thread

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import com.squareup.moshi.JsonAdapter
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.databinding.FragmentThreadBinding
import io.numbers.mediant.model.Meta
import io.numbers.mediant.ui.dialogs.ConfirmationDialogFragment
import io.numbers.mediant.ui.dialogs.DialogListener
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.timestampToString
import io.textile.textile.FeedItemData
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ThreadFragment : Fragment(), FeedItemListener {

    private val threadViewModel: ThreadViewModel by viewModel()
    private val preferenceHelper: PreferenceHelper by inject()
    private val textileService: TextileService by inject()
    private val metaJsonAdapter: JsonAdapter<Meta> by inject()

    private lateinit var adapter: ThreadRecyclerViewAdapter
    private lateinit var binding: FragmentThreadBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thread, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = threadViewModel

        setThreadIdToViewModel()
        if (!threadViewModel.isPersonal) setHasOptionsMenu(true)

        adapter =
            ThreadRecyclerViewAdapter(
                textileService,
                metaJsonAdapter,
                this,
                threadViewModel.isPersonal
            )
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    private fun setThreadIdToViewModel() {
        val threadId = arguments?.let { ThreadFragmentArgs.fromBundle(it).threadId }
            ?: preferenceHelper.personalThreadId
        threadId?.also { threadViewModel.threadId = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!threadViewModel.isPersonal) threadViewModel.threadName.observe(
            viewLifecycleOwner,
            Observer {
                (activity as AppCompatActivity).supportActionBar?.title = it
            })
        threadViewModel.feedList.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.thread_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navToThreadInformation -> showInformation()
            R.id.inviteOthers -> inviteOthers()
            R.id.refresh -> threadViewModel.loadFeedList()
            R.id.scrollToTop -> smoothScrollToTop()
            R.id.leaveThread -> leaveThread()
        }
        return true
    }

    private fun showInformation() {
        findNavController().navigate(
            ThreadFragmentDirections.actionThreadFragmentToThreadInformationFragment(threadViewModel.threadId)
        )
    }

    private fun inviteOthers() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textileService.addExternalInvite(threadViewModel.threadId))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.invite_others)))
    }

    private fun leaveThread() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                textileService.leaveThread(threadViewModel.threadId)
                dialog.dismiss()
                findNavController().popBackStack()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }
        ConfirmationDialogFragment().apply {
            title = R.string.title_leave_thread_confirmation
            listener = dialogCallback
        }.show(childFragmentManager, ConfirmationDialogFragment::javaClass.name)
    }

    override fun onShowProof(feedItemData: FeedItemData) {
        // TODO: use block API after available: https://github.com/textileio/android-textile/issues/15
        findNavController().navigate(
            if (threadViewModel.isPersonal) MainFragmentDirections.actionMainFragmentToMediaDetailsFragment(
                feedItemData.files.getFiles(0).file.hash,
                feedItemData.files.caption,
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.block
            )
            else ThreadFragmentDirections.actionThreadFragmentToMediaDetailsFragment(
                feedItemData.files.getFiles(0).file.hash,
                feedItemData.files.caption,
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.block
            )
        )
    }

    override fun onPublish(feedItemData: FeedItemData) {
        findNavController().navigate(
            // TODO: use block API after available: https://github.com/textileio/android-textile/issues/15
            MainFragmentDirections.actionMainFragmentToPublishingFragment(
                feedItemData.files.data,
                feedItemData.files.getFiles(0).file.hash,
                feedItemData.files.caption,
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date)
            )
        )
    }

    override fun onDelete(feedItemData: FeedItemData) {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                threadViewModel.deleteFile(feedItemData.files)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }
        ConfirmationDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ConfirmationDialogFragment::javaClass.name
        )
    }

    private fun smoothScrollToTop() {
        binding.recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply { targetPosition = 0 })
    }
}