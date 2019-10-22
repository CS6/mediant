package io.numbers.mediant.ui.main.thread_list.thread_invite_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerDialogFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.DialogThreadInviteBinding
import io.numbers.mediant.ui.dialogs.DialogListener
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadInviteDialogFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadInviteDialogViewModel

    lateinit var listener: DialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadInviteDialogViewModel::class.java]
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding: DialogThreadInviteBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_thread_invite,
                null,
                false
            )
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(binding.root)
                .setTitle(R.string.accept_invite)
                .setPositiveButton(R.string.add) { _, _ -> listener.onDialogPositiveClick(this) }
                .setNegativeButton(R.string.cancel) { _, _ -> listener.onDialogNegativeClick(this) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}