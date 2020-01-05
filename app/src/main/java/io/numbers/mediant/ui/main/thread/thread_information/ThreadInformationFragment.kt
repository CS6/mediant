package io.numbers.mediant.ui.main.thread.thread_information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentThreadInformationBinding
import io.numbers.mediant.ui.dialogs.DialogListener
import io.numbers.mediant.ui.main.thread_naming_dialog.ThreadNamingDialogFragment
import io.numbers.mediant.viewmodel.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class ThreadInformationFragment : Fragment() {

    private val threadInformationViewModel: ThreadInformationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentThreadInformationBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_thread_information, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = threadInformationViewModel
        initLiveData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        threadInformationViewModel.showNamingDialogEvent.observe(viewLifecycleOwner, EventObserver {
            showThreadNamingDialog()
        })
    }

    private fun initLiveData() {
        arguments?.let {
            threadInformationViewModel.threadId.value =
                ThreadInformationFragmentArgs.fromBundle(it).threadId
        }
    }

    private fun showThreadNamingDialog() {
        val dialogCallback = object : DialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                val threadName =
                    (dialog as ThreadNamingDialogFragment).threadNamingDialogViewModel.threadName.value
                        ?: ""
                threadInformationViewModel.setThreadName(threadName)
                dialog.dismiss()
            }

            override fun onDialogNegativeClick(dialog: DialogFragment) {
                dialog.dismiss()
            }
        }

        ThreadNamingDialogFragment().apply { listener = dialogCallback }.show(
            childFragmentManager,
            ThreadNamingDialogFragment::javaClass.name
        )
    }
}