package io.numbers.mediant.di

import android.app.Application
import com.htc.htcwalletsdk.Export.HtcWalletSdkManager
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.numbers.mediant.api.canon_camera_control.BASE_URL
import io.numbers.mediant.api.canon_camera_control.CanonCameraControlApi
import io.numbers.mediant.api.canon_camera_control.CanonCameraControlService
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.util.PreferenceHelper
import io.textile.textile.Textile
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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
    fun providePreferenceHelper(application: Application) = PreferenceHelper(application)

    @Singleton
    @Provides
    fun provideTextileService(
        preferenceHelper: PreferenceHelper,
        application: Application
    ) = TextileService(Textile.instance(), preferenceHelper, application)

    @Singleton
    @Provides
    fun provideProofModeService(
        application: Application,
        preferenceHelper: PreferenceHelper
    ) = ProofModeService(application, preferenceHelper)

    @Singleton
    @Provides
    fun provideZionService(application: Application) = ZionService(
        HtcWalletSdkManager.getInstance(),
        application
    )

    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideCanonCCApi(retrofit: Retrofit): CanonCameraControlApi =
        retrofit.create(CanonCameraControlApi::class.java)

    @Singleton
    @Provides
    fun provideCanonCCService(canonCameraControlApi: CanonCameraControlApi) =
        CanonCameraControlService(canonCameraControlApi)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()
}