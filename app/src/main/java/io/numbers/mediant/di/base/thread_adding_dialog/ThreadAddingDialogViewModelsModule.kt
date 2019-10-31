package io.numbers.mediant.di.base.thread_adding_dialog

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread_list.thread_adding_dialog.ThreadAddingDialogViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadAddingDialogViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadAddingDialogViewModel::class)
    abstract fun bindViewModel(viewModel: ThreadAddingDialogViewModel): ViewModel
}