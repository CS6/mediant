package io.numbers.mediant.di.base.validation

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.validation.ValidationViewModel


@Suppress("UNUSED")
@Module
abstract class ValidationViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ValidationViewModel::class)
    abstract fun bindViewModel(viewModel: ValidationViewModel): ViewModel
}