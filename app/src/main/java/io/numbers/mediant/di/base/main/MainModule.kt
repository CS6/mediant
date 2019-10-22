package io.numbers.mediant.di.base.main

import dagger.Module
import dagger.Provides
import io.numbers.mediant.R
import io.numbers.mediant.ui.main.thread.ThreadFragment
import io.numbers.mediant.ui.main.thread_list.ThreadListFragment
import io.numbers.mediant.ui.tab.Tab
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
class MainModule {

    @ExperimentalCoroutinesApi
    @Provides
    fun provideTabList() =
        listOf(
            Tab(R.string.feeds, ThreadListFragment()),
            Tab(R.string.storage, ThreadFragment())
        )
}