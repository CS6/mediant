package io.numbers.mediant.ui.dialogs

import androidx.fragment.app.DialogFragment

interface DialogListener {
    fun onDialogPositiveClick(dialog: DialogFragment)
    fun onDialogNegativeClick(dialog: DialogFragment)
}