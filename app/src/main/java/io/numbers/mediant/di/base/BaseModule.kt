package io.numbers.mediant.di.base

import dagger.Module
import dagger.Provides
import io.numbers.mediant.ui.BaseActivity
import io.numbers.mediant.util.PermissionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
class BaseModule {

    @ExperimentalCoroutinesApi
    @Provides
    fun providePermissionManager(baseActivity: BaseActivity) = PermissionManager(baseActivity)
}