package io.numbers.mediant.ui.main.thread_naming_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.numbers.mediant.R
import io.numbers.mediant.databinding.DialogThreadNamingBinding
import io.numbers.mediant.ui.dialogs.DialogListener
import org.koin.android.viewmodel.ext.android.viewModel

class ThreadNamingDialogFragment : DialogFragment() {

    val threadNamingDialogViewModel: ThreadNamingDialogViewModel by viewModel()

    lateinit var listener: DialogListener

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding: DialogThreadNamingBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_thread_naming,
                null,
                false
            )
            binding.lifecycleOwner = this
            binding.viewModel = threadNamingDialogViewModel
            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(binding.root)
                .setTitle(R.string.new_thread_name)
                .setPositiveButton(R.string.confirm) { _, _ -> listener.onDialogPositiveClick(this) }
                .setNegativeButton(R.string.cancel) { _, _ -> listener.onDialogNegativeClick(this) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}