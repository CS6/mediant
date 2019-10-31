package io.numbers.mediant.di.base.onboarding_end_page

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.onboarding.end_page.OnboardingEndPageViewModel

@Suppress("UNUSED")
@Module
abstract class OnboardingEndPageViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingEndPageViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: OnboardingEndPageViewModel): ViewModel
}