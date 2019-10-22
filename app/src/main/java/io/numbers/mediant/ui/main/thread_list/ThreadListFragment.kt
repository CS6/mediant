package io.numbers.mediant.ui.main.thread_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentThreadListBinding
import io.numbers.mediant.ui.listeners.DialogListener
import io.numbers.mediant.ui.listeners.ItemClickListener
import io.numbers.mediant.ui.listeners.ItemMenuClickListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.ui.main.thread_list.thread_adding_dialog.ThreadAddingDialogFragment
import io.numbers.mediant.ui.main.thread_list.thread_creation_dialog.ThreadCreationDialogFragment
import io.numbers.mediant.ui.main.thread_list.thread_invite_dialog.ThreadInviteDialogFragment
import io.numbers.mediant.ui.tab.TabFragment
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import kotlinx.coroutines.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ThreadListFragment : DaggerFragment(), TabFragment, ItemClickListener, ItemMenuClickListener,
    CoroutineScope by MainScope() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadListViewModel

    private val adapter = ThreadListRecyclerViewAdapter(this, this)

    private lateinit var binding: FragmentThreadListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thread_list, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.threadList.observe(viewLifecycleOwner, Observer { adapter.data = it })
        viewModel.openDialog.observe(viewLifecycleOwner, EventObserver { showThreadAddingDialog() })
    }

    private fun showThreadAddingDialog() {
        val dialogCallback = object : ThreadAddingDialogFragment.ThreadAddingDialogListener {
            override fun createThread(dialog: BottomSheetDialogFragment) {
                showThreadCreationDialog()
                dialog.dismiss()
            }

            override fun acceptInvite(dialog: BottomSheetDialogFragment) {
                showThreadInviteDialog()
                dialog.dismiss()
            }
        }
        ThreadAddingDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ThreadAddingDialogFragment::javaClass.name
        )
    }

    private fun showThreadCreationDialog() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                val threadName =
                    (dialog as ThreadCreationDialogFragment).viewModel.threadName.value ?: ""
                viewModel.addThread(threadName)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }

        ThreadCreationDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ThreadCreationDialogFragment::javaClass.name
        )
    }

    private fun showThreadInviteDialog() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                dialog as ThreadInviteDialogFragment
                val inviteId = dialog.viewModel.inviteId.value ?: ""
                val inviteKey = dialog.viewModel.inviteKey.value ?: ""
                launch(Dispatchers.IO) {
                    try {
                        viewModel.acceptInvite(inviteId, inviteKey)
                        showSnackBar(R.string.message_accepting_thread_invite)
                    } catch (e: Exception) {
                        showErrorSnackBar(e.message)
                    }
                }
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }
        ThreadInviteDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ThreadInviteDialogFragment::javaClass.name
        )
    }

    override fun onItemClick(position: Int) {
        MainFragmentDirections.actionMainFragmentToThreadFragment(
            adapter.data[position].id,
            adapter.data[position].name
        ).also { findNavController().navigate(it) }
    }

    override fun onItemMenuClick(position: Int, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_thread_info -> {
            }
            R.id.action_leave_thread -> viewModel.leaveThread(adapter.data[position])
        }
        return true
    }

    override fun smoothScrollToTop() {
        binding.recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply { targetPosition = 0 })
    }

    private fun showSnackBar(@StringRes message: Int, @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_LONG) =
        view?.let {
            val snackbar = Snackbar.make(it, message, duration)
            snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }
            snackbar.show()
        }

    private fun showErrorSnackBar(errorMessage: String?) {
        if (errorMessage.isNullOrEmpty()) {
            showSnackBar(R.string.message_thread_invite_error)
        } else {
            view?.let {
                val snackbar = Snackbar.make(it, errorMessage, Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }
                snackbar.show()
            }
        }
    }
}