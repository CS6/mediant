package io.numbers.mediant.ui.main.thread_list.thread_adding_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.DialogThreadAddingBinding
import io.numbers.mediant.viewmodel.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel

class ThreadAddingDialogFragment : BottomSheetDialogFragment() {

    private val threadAddingDialogViewModel: ThreadAddingDialogViewModel by viewModel()

    lateinit var listener: ThreadAddingDialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogThreadAddingBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_thread_adding, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = threadAddingDialogViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        threadAddingDialogViewModel.createThreadEvent.observe(
            viewLifecycleOwner,
            EventObserver { listener.createThread(this) })
        threadAddingDialogViewModel.acceptInviteEvent.observe(
            viewLifecycleOwner,
            EventObserver { listener.acceptInvite(this) })
    }

    interface ThreadAddingDialogListener {
        fun createThread(dialog: BottomSheetDialogFragment)
        fun acceptInvite(dialog: BottomSheetDialogFragment)
    }
}