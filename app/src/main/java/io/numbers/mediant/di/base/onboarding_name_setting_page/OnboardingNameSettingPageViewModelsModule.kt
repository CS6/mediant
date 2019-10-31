package io.numbers.mediant.di.base.onboarding_name_setting_page

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.onboarding.name_setting_page.OnboardingNameSettingPageViewModel

@Suppress("UNUSED")
@Module
abstract class OnboardingNameSettingPageViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingNameSettingPageViewModel::class)
    abstract fun bindViewModel(viewModel: OnboardingNameSettingPageViewModel): ViewModel
}