package io.numbers.mediant.ui.snackbar

import android.view.View

interface ShowableSnackbar {
    fun showSnackbar(view: View, snackbarArgs: SnackbarArgs)
    fun showErrorSnackbar(view: View, exception: Exception)
}