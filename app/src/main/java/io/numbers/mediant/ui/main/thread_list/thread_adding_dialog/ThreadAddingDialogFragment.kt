package io.numbers.mediant.ui.main.thread_list.thread_adding_dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.DaggerAppCompatActivity
import io.numbers.mediant.R
import io.numbers.mediant.databinding.DialogThreadAddingBinding
import io.numbers.mediant.viewmodel.EventObserver
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadAddingDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadAddingDialogViewModel

    lateinit var listener: ThreadAddingDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as DaggerAppCompatActivity).androidInjector().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadAddingDialogViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogThreadAddingBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_thread_adding, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.createThreadEvent.observe(
            viewLifecycleOwner,
            EventObserver { listener.createThread(this) })
        viewModel.acceptInviteEvent.observe(
            viewLifecycleOwner,
            EventObserver { listener.acceptInvite(this) })
    }

    interface ThreadAddingDialogListener {
        fun createThread(dialog: BottomSheetDialogFragment)
        fun acceptInvite(dialog: BottomSheetDialogFragment)
    }
}