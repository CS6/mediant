package io.numbers.mediant.ui.snackbar

import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.numbers.mediant.R

class DefaultShowableSnackbar : ShowableSnackbar {

    override fun showSnackbar(view: View, snackbarArgs: SnackbarArgs) {
        val snackbar = Snackbar.make(view, snackbarArgs.message, snackbarArgs.duration)
        if (snackbarArgs.duration == Snackbar.LENGTH_INDEFINITE) snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }
        snackbar.show()
    }

    override fun showErrorSnackbar(view: View, exception: Exception) {
        exception.message?.also {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        } ?: run {
            Snackbar.make(view, R.string.message_general_error, Snackbar.LENGTH_LONG).show()
        }
    }
}