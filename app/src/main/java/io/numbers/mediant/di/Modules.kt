package io.numbers.mediant.di

import com.htc.htcwalletsdk.Export.HtcWalletSdkManager
import com.squareup.moshi.Moshi
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.ui.BaseViewModel
import io.numbers.mediant.ui.initialization.InitializationViewModel
import io.numbers.mediant.ui.main.MainViewModel
import io.numbers.mediant.ui.main.thread.ThreadViewModel
import io.numbers.mediant.ui.main.thread.thread_information.ThreadInformationViewModel
import io.numbers.mediant.ui.main.thread_list.ThreadListViewModel
import io.numbers.mediant.ui.main.thread_list.thread_adding_dialog.ThreadAddingDialogViewModel
import io.numbers.mediant.ui.main.thread_list.thread_invite_dialog.ThreadInviteDialogViewModel
import io.numbers.mediant.ui.main.thread_naming_dialog.ThreadNamingDialogViewModel
import io.numbers.mediant.ui.media_details.MediaDetailsViewModel
import io.numbers.mediant.ui.onboarding.end_page.OnboardingEndPageViewModel
import io.numbers.mediant.ui.onboarding.name_setting_page.OnboardingNameSettingPageViewModel
import io.numbers.mediant.ui.onboarding.zion_setting_page.OnboardingZionSettingPageViewModel
import io.numbers.mediant.ui.permission_rationale.PermissionRationaleViewModel
import io.numbers.mediant.ui.publishing.PublishingViewModel
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PreferenceHelper
import io.textile.textile.Textile
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { PreferenceHelper(androidApplication()) }
    single { TextileService(Textile.instance(), get(), androidApplication()) }
    single { ProofModeService(androidApplication()) }
    single { ZionService(HtcWalletSdkManager.getInstance(), androidApplication()) }
    single { Moshi.Builder().build() }
    single { get<Moshi>().adapter<Meta>(Meta::class.java) }
    single { PermissionManager(androidContext()) }

    viewModel { BaseViewModel(get()) }
    viewModel { InitializationViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { MediaDetailsViewModel(get(), get()) }
    viewModel { OnboardingEndPageViewModel() }
    viewModel { OnboardingNameSettingPageViewModel(get()) }
    viewModel { OnboardingZionSettingPageViewModel(get(), get()) }
    viewModel { PermissionRationaleViewModel() }
    viewModel { PublishingViewModel(get(), get()) }
    viewModel { ThreadViewModel(get(), get()) }
    viewModel { ThreadAddingDialogViewModel() }
    viewModel { ThreadInformationViewModel(get()) }
    viewModel { ThreadInviteDialogViewModel() }
    viewModel { ThreadListViewModel(get()) }
    viewModel { ThreadNamingDialogViewModel() }
}