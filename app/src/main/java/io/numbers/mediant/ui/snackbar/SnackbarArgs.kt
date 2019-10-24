package io.numbers.mediant.ui.snackbar

import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

data class SnackbarArgs(@StringRes val message: Int, @BaseTransientBottomBar.Duration val duration: Int = Snackbar.LENGTH_LONG)