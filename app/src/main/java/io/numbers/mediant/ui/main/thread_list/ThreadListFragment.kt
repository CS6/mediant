package io.numbers.mediant.ui.main.thread_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentThreadListBinding
import io.numbers.mediant.ui.dialogs.DialogListener
import io.numbers.mediant.ui.listeners.ItemClickListener
import io.numbers.mediant.ui.main.MainFragmentDirections
import io.numbers.mediant.ui.main.thread_list.thread_adding_dialog.ThreadAddingDialogFragment
import io.numbers.mediant.ui.main.thread_list.thread_invite_dialog.ThreadInviteDialogFragment
import io.numbers.mediant.ui.main.thread_naming_dialog.ThreadNamingDialogFragment
import io.numbers.mediant.ui.snackbar.DefaultShowableSnackbar
import io.numbers.mediant.ui.snackbar.ShowableSnackbar
import io.numbers.mediant.viewmodel.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class ThreadListFragment : Fragment(), ItemClickListener,
    ShowableSnackbar by DefaultShowableSnackbar() {

    private val threadListViewModel: ThreadListViewModel by viewModel()

    private val adapter = ThreadListRecyclerViewAdapter(this)

    private lateinit var binding: FragmentThreadListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_thread_list, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = threadListViewModel
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        threadListViewModel.threadList.observe(
            viewLifecycleOwner,
            Observer { adapter.submitList(it) })
        threadListViewModel.openDialogEvent.observe(
            viewLifecycleOwner, EventObserver { showThreadAddingDialog() })
        threadListViewModel.showSnackbar.observe(
            viewLifecycleOwner,
            EventObserver { showSnackbar(view, it) })
        threadListViewModel.showErrorSnackbar.observe(
            viewLifecycleOwner, EventObserver { showErrorSnackbar(view, it) }
        )
    }

    private fun showThreadAddingDialog() {
        val dialogCallback = object : ThreadAddingDialogFragment.ThreadAddingDialogListener {
            override fun createThread(dialog: BottomSheetDialogFragment) {
                showThreadNamingDialog()
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

    private fun showThreadNamingDialog() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                val threadName =
                    (dialog as ThreadNamingDialogFragment).threadNamingDialogViewModel.threadName.value
                        ?: ""
                threadListViewModel.addThread(threadName)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) = dialog.dismiss()
        }

        ThreadNamingDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ThreadNamingDialogFragment::javaClass.name
        )
    }

    private fun showThreadInviteDialog() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                dialog as ThreadInviteDialogFragment
                val inviteId = dialog.threadInviteDialogViewModel.inviteId.value ?: ""
                val inviteKey = dialog.threadInviteDialogViewModel.inviteKey.value ?: ""
                threadListViewModel.acceptInvite(inviteId, inviteKey)
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
            adapter.currentList[position].id,
            adapter.currentList[position].name
        ).also { findNavController().navigate(it) }
    }
}