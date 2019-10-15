package io.numbers.mediant.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import io.numbers.mediant.api.TextileService
import io.textile.textile.Textile
import javax.inject.Singleton

// Provides all application-level dependencies, such as Retrofit, Textile, etc.

@Module
class AppModule {
    // Generally provide dependencies with @Singleton annotation due to AppComponent owns the
    // singleton scope and Dagger will check which providers also OWNS the singleton scope.
    // Therefore, actually we can use any name of scope to annotate AppComponent and the providers
    // in AppModule. For example,
    // ```
    // @Singleton
    // @Provides
    // fun provideSomeString() = "some string"
    // ```

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    @Singleton
    @Provides
    fun provideTextileService(
        sharedPreferences: SharedPreferences,
        application: Application
    ): TextileService = TextileService(Textile.instance(), sharedPreferences, application)
}