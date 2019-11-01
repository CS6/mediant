package io.numbers.mediant.di.base.onboarding_zion_setting_page

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.numbers.mediant.di.ViewModelKey
import io.numbers.mediant.ui.onboarding.zion_setting_page.OnboardingZionSettingPageViewModel

@Suppress("UNUSED")
@Module
abstract class OnboardingZionSettingPageViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingZionSettingPageViewModel::class)
    abstract fun bindViewModel(viewModel: OnboardingZionSettingPageViewModel): ViewModel
}