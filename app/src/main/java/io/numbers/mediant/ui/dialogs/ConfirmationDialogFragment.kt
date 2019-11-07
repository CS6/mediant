package io.numbers.mediant.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.numbers.mediant.R

class ConfirmationDialogFragment : DialogFragment() {

    @StringRes
    var title: Int = R.string.title_deletion_confirmation
    lateinit var listener: DialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            builder.setTitle(title)
                .setPositiveButton(R.string.title_positive_confirmation) { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}