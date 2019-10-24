package io.numbers.mediant.ui.main.thread

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.databinding.FragmentThreadBinding
import io.numbers.mediant.ui.dialogs.ConfirmationDialogFragment
import io.numbers.mediant.ui.dialogs.DialogListener
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.ui.tab.TabFragment
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.timestampToString
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import io.textile.textile.FeedItemData
import javax.inject.Inject

class ThreadFragment : DaggerFragment(), TabFragment, FeedItemListener {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadViewModel

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var textileService: TextileService

    private lateinit var adapter: ThreadRecyclerViewAdapter
    private lateinit var binding: FragmentThreadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thread, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setThreadIdToViewModel()
        if (!viewModel.isPersonal) setHasOptionsMenu(true)

        adapter = ThreadRecyclerViewAdapter(textileService, this, viewModel.isPersonal)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    private fun setThreadIdToViewModel() {
        val threadId = arguments?.let { ThreadFragmentArgs.fromBundle(it).threadId }
            ?: preferenceHelper.personalThreadId
        threadId?.also { viewModel.threadId = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!viewModel.isPersonal) viewModel.threadName.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity).supportActionBar?.title = it
        })
        viewModel.feedList.observe(viewLifecycleOwner, Observer { adapter.data = it })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.thread_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionThreadInformation -> showInformation()
            R.id.actionInviteOthers -> inviteOthers()
            R.id.actionRefresh -> viewModel.loadFeedList()
            R.id.actionScrollToTop -> smoothScrollToTop()
            R.id.actionLeaveThread -> leaveThread()
        }
        return true
    }

    private fun showInformation() {
        findNavController().navigate(
            if (viewModel.isPersonal) MainFragmentDirections.actionMainFragmentToThreadInformationFragment(
                viewModel.threadId
            )
            else ThreadFragmentDirections.actionThreadFragmentToThreadInformationFragment(
                viewModel.threadId
            )
        )
    }

    private fun inviteOthers() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textileService.addExternalInvite(viewModel.threadId))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.invite_others)))
    }

    private fun leaveThread() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                textileService.leaveThread(viewModel.threadId)
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
            if (viewModel.isPersonal) MainFragmentDirections.actionMainFragmentToMediaDetailsFragment(
                textileService.getFileIpfsPath(feedItemData.files),
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.files.caption,
                feedItemData.block
            )
            else ThreadFragmentDirections.actionThreadFragmentToMediaDetailsFragment(
                textileService.getFileIpfsPath(feedItemData.files),
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.files.caption,
                feedItemData.block
            )
        )
    }

    override fun onPublish(feedItemData: FeedItemData) {
        findNavController().navigate(
            // TODO: use block API after available: https://github.com/textileio/android-textile/issues/15
            MainFragmentDirections.actionMainFragmentToPublishingFragment(
                feedItemData.files.data,
                textileService.getFileIndex(feedItemData.files),
                feedItemData.files.user.name,
                timestampToString(feedItemData.files.date),
                feedItemData.files.caption
            )
        )
    }

    override fun onDelete(feedItemData: FeedItemData) {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                viewModel.deleteFile(feedItemData.files)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }
        ConfirmationDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ConfirmationDialogFragment::javaClass.name
        )
    }

    override fun smoothScrollToTop() {
        binding.recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply { targetPosition = 0 })
    }
}