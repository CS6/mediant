package io.numbers.mediant.di.base.thread_invite_dialog

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.main.thread_list.thread_invite_dialog.ThreadInviteDialogViewModel

@Suppress("UNUSED")
@Module
abstract class ThreadInviteDialogViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ThreadInviteDialogViewModel::class)
    abstract fun bindViewModel(viewModel: ThreadInviteDialogViewModel): ViewModel
}