package io.numbers.mediant.di.base

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.numbers.mediant.di.base.initialization.InitializationViewModelsModule
import io.numbers.mediant.di.base.main.MainViewModelsModule
import io.numbers.mediant.di.base.media_details.MediaDetailsViewModelsModule
import io.numbers.mediant.di.base.onboarding_end_page.OnboardingEndPageViewModelsModule
import io.numbers.mediant.di.base.permission_rationale.PermissionRationaleViewModelsModule
import io.numbers.mediant.di.base.publishing.PublishingViewModelsModule
import io.numbers.mediant.di.base.thread.ThreadViewModelsModule
import io.numbers.mediant.di.base.thread_adding_dialog.ThreadAddingDialogViewModelsModule
import io.numbers.mediant.di.base.thread_information.ThreadInformationViewModelsModule
import io.numbers.mediant.di.base.thread_invite_dialog.ThreadInviteDialogViewModelsModule
import io.numbers.mediant.di.base.thread_list.ThreadListViewModelsModule
import io.numbers.mediant.di.base.thread_naming_dialog.ThreadNamingDialogViewModelsModule
import io.numbers.mediant.ui.initialization.InitializationFragment
import io.numbers.mediant.ui.main.MainFragment
import io.numbers.mediant.ui.main.thread.ThreadFragment
import io.numbers.mediant.ui.main.thread.thread_information.ThreadInformationFragment
import io.numbers.mediant.ui.main.thread_list.ThreadListFragment
import io.numbers.mediant.ui.main.thread_list.thread_adding_dialog.ThreadAddingDialogFragment
import io.numbers.mediant.ui.main.thread_list.thread_invite_dialog.ThreadInviteDialogFragment
import io.numbers.mediant.ui.main.thread_naming_dialog.ThreadNamingDialogFragment
import io.numbers.mediant.ui.media_details.MediaDetailsFragment
import io.numbers.mediant.ui.onboarding.end_page.OnboardingEndPageFragment
import io.numbers.mediant.ui.permission_rationale.PermissionRationaleFragment
import io.numbers.mediant.ui.publishing.PublishingFragment
import io.numbers.mediant.ui.settings.PreferencesFragment

// Provides all fragments extending from DaggerFragment in BaseActivity scope as Dagger client.

@Suppress("UNUSED")
@Module
abstract class BaseFragmentBuildersModule {

    // 1. Let Dagger know InitializationFragment is a potential client. Therefore, we do NOT need to
    //    write `AndroidInjection.inject(this)` in InitializationFragment.onCreate() method.
    // 2. Dagger will generate InitializationFragmentSubcomponent under the hook with the following
    //    method.
    @ContributesAndroidInjector(modules = [InitializationViewModelsModule::class])
    abstract fun contributeInitializationFragment(): InitializationFragment

    @ContributesAndroidInjector(modules = [MainViewModelsModule::class])
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector(modules = [ThreadViewModelsModule::class])
    abstract fun contributeThreadFragment(): ThreadFragment

    @ContributesAndroidInjector(modules = [ThreadListViewModelsModule::class])
    abstract fun contributeThreadListFragment(): ThreadListFragment

    @ContributesAndroidInjector
    abstract fun contributePreferencesFragment(): PreferencesFragment

    @ContributesAndroidInjector(modules = [ThreadAddingDialogViewModelsModule::class])
    abstract fun contributeThreadAddingDialogFragment(): ThreadAddingDialogFragment

    @ContributesAndroidInjector(modules = [ThreadNamingDialogViewModelsModule::class])
    abstract fun contributeThreadCreationDialogFragment(): ThreadNamingDialogFragment

    @ContributesAndroidInjector(modules = [ThreadInviteDialogViewModelsModule::class])
    abstract fun contributeThreadInviteDialogFragment(): ThreadInviteDialogFragment

    @ContributesAndroidInjector(modules = [ThreadInformationViewModelsModule::class])
    abstract fun contributeThreadInformationFragment(): ThreadInformationFragment

    @ContributesAndroidInjector(modules = [PublishingViewModelsModule::class])
    abstract fun contributePublishingFragment(): PublishingFragment

    @ContributesAndroidInjector(modules = [MediaDetailsViewModelsModule::class])
    abstract fun contributeMediaDetailsFragment(): MediaDetailsFragment

    @ContributesAndroidInjector(modules = [PermissionRationaleViewModelsModule::class])
    abstract fun contributePermissionRationaleFragment(): PermissionRationaleFragment

    @ContributesAndroidInjector(modules = [OnboardingEndPageViewModelsModule::class])
    abstract fun contributeOnboardingEndPageFragment(): OnboardingEndPageFragment

    // Add new Fragments as Dagger client here. Dagger will automatically generate
    // XXXFragmentSubcomponents.
}