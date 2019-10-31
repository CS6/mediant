package io.numbers.mediant.di.base.thread_naming_dialog

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread_naming_dialog.ThreadNamingDialogViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadNamingDialogViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadNamingDialogViewModel::class)
    abstract fun bindViewModel(viewModel: ThreadNamingDialogViewModel): ViewModel
}