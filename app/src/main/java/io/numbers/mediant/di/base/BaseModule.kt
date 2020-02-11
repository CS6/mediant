package io.numbers.mediant.di.base

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.numbers.mediant.api.MediantService
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.session_based_signature.SessionBasedSignatureService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.api.restful.RestfulService
import io.numbers.mediant.ui.BaseActivity
import io.numbers.mediant.util.PermissionManager
import io.numbers.mediant.util.PreferenceHelper

@Suppress("UNUSED")
@Module
class BaseModule {

    @Provides
    fun providePermissionManager(baseActivity: BaseActivity) = PermissionManager(baseActivity)

    @Provides
    fun provideMediantInstance(
        textileService: TextileService,
        sessionBasedSignatureService: SessionBasedSignatureService,
        proofModeService: ProofModeService,
        zionService: ZionService,
        restfulService: RestfulService,
        preferenceHelper: PreferenceHelper,
        moshi: Moshi
    ) = MediantService(textileService, proofModeService, sessionBasedSignatureService, zionService, restfulService, preferenceHelper, moshi)
}