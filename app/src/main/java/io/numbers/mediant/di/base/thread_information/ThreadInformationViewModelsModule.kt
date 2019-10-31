package io.numbers.mediant.di.base.thread_information

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread.thread_information.ThreadInformationViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadInformationViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadInformationViewModel::class)
    abstract fun bindViewModel(viewModel: ThreadInformationViewModel): ViewModel
}