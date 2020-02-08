package io.numbers.mediant.util

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import io.numbers.mediant.R
import org.witness.proofmode.crypto.PgpUtils
import javax.inject.Inject

class PreferenceHelper @Inject constructor(application: Application) {

    val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    val preferenceKeyUserName =
        application.applicationContext.resources.getString(R.string.key_user_name)
    val preferenceKeyWalletRecoveryPhrase =
        application.applicationContext.resources.getString(R.string.key_wallet_recovery_phrase)
    val preferenceKeyPersonalThreadId =
        application.applicationContext.resources.getString(R.string.key_personal_thread_id)
    val preferenceKeyProofModePgpPassword =
        application.applicationContext.resources.getString(R.string.key_proofmode_pgp_password)
    val preferenceKeyProofModePgpPublicKey =
        application.applicationContext.resources.getString(R.string.key_proofmode_pgp_public_key)
    val preferenceKeyInfoSnapshotDuration =
        application.applicationContext.resources.getString(R.string.key_info_snapshot_duration)
    val preferenceKeySignWithZion =
        application.applicationContext.resources.getString(R.string.key_sign_with_zion)
    val preferenceKeyConnectToCanonCamera =
        application.applicationContext.resources.getString(R.string.key_connect_to_canon_camera)
    val preferenceKeyEnablePollingCanonCameraStatus =
        application.applicationContext.resources.getString(R.string.key_enable_polling_canon_camera_status)

    var userName: String?
        get() = sharedPreferences.getString(preferenceKeyUserName, "")
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyUserName, value
        ).apply()
    var walletRecoveryPhrase: String?
        get() = sharedPreferences.getString(preferenceKeyWalletRecoveryPhrase, null)
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyWalletRecoveryPhrase, value
        ).apply()
    var personalThreadId: String?
        get() = sharedPreferences.getString(preferenceKeyPersonalThreadId, null)
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyPersonalThreadId,
            value
        ).apply()
    var proofModePgpPassword: String?
        get() = sharedPreferences.getString(preferenceKeyProofModePgpPassword, null)
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyProofModePgpPassword,
            value
        ).apply()
    var proofModePgpPublicKey: String?
        get() = sharedPreferences.getString(preferenceKeyProofModePgpPublicKey, null)
        set(value) = sharedPreferences.edit().putString(
            preferenceKeyProofModePgpPublicKey,
            value
        ).apply()
    var infoSnapshotDuration: Int
        get() = sharedPreferences.getInt(preferenceKeyInfoSnapshotDuration, 7)
        set(value) = sharedPreferences.edit().putInt(
            preferenceKeyInfoSnapshotDuration,
            value
        ).apply()
    var signWithZion: Boolean
        get() = sharedPreferences.getBoolean(preferenceKeySignWithZion, false)
        set(value) = sharedPreferences.edit().putBoolean(preferenceKeySignWithZion, value).apply()
    var enablePollingCanonCameraStatus: Boolean
        get() = sharedPreferences.getBoolean(preferenceKeyEnablePollingCanonCameraStatus, false)
        set(value) = sharedPreferences.edit().putBoolean(
            preferenceKeyEnablePollingCanonCameraStatus, value
        ).apply()

    init {
        sharedPreferences.edit().putString(
            preferenceKeyProofModePgpPassword,
            PgpUtils.DEFAULT_PASSWORD
        ).apply()
        sharedPreferences.edit().putString(
            preferenceKeyProofModePgpPublicKey,
            PgpUtils.getInstance(application.applicationContext).publicKey
        ).apply()
    }
}