package io.numbers.mediant.ui.main.thread_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.DaggerFragment
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
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadListFragment : DaggerFragment(), ItemClickListener,
    ShowableSnackbar by DefaultShowableSnackbar() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadListViewModel

    private val adapter = ThreadListRecyclerViewAdapter(this)

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
        viewModel.threadList.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
        viewModel.openDialogEvent.observe(
            viewLifecycleOwner, EventObserver { showThreadAddingDialog() })
        viewModel.showSnackbar.observe(viewLifecycleOwner, EventObserver { showSnackbar(view, it) })
        viewModel.showErrorSnackbar.observe(
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
                    (dialog as ThreadNamingDialogFragment).viewModel.threadName.value ?: ""
                viewModel.addThread(threadName)
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
                val inviteId = dialog.viewModel.inviteId.value ?: ""
                val inviteKey = dialog.viewModel.inviteKey.value ?: ""
                viewModel.acceptInvite(inviteId, inviteKey)
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