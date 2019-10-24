package io.numbers.mediant.api.zion

import android.app.Application
import com.htc.htcwalletsdk.Export.HtcWalletSdkManager
import com.htc.htcwalletsdk.Export.RESULT
import com.htc.htcwalletsdk.Native.Type.ByteArrayHolder
import io.numbers.mediant.BuildConfig.APPLICATION_ID
import io.numbers.mediant.util.getHashFromString
import org.json.JSONObject
import org.witness.proofmode.crypto.HashUtils
import org.witness.proofmode.util.DeviceInfo
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val ZION_WALLET_NAME = APPLICATION_ID
const val ETHEREUM_TYPE = 60

class ZionService @Inject constructor(
    private val zkma: HtcWalletSdkManager,
    private val application: Application
) {

    private var uniqueId: Long? = null

    val deviceName: String
        get() = DeviceInfo.getDeviceInfo(
            application.applicationContext,
            DeviceInfo.Device.DEVICE_HARDWARE_MODEL
        ).toUpperCase(Locale.ENGLISH)

    val isHtcExodus1: Boolean
        get() = "HTC EXODUS 1" == deviceName

    fun initZkma() = zkma.init(application.applicationContext).also {
        if (it == RESULT.SUCCESS) createWalletSeed()
    }

    private fun createWalletSeed() {
        val walletNameHash = getHashFromString(ZION_WALLET_NAME)
        uniqueId = zkma.register(ZION_WALLET_NAME, walletNameHash)
        uniqueId?.also {
            when (val result = zkma.createSeed(it)) {
                RESULT.SUCCESS -> Timber.i("Zion seed created successfully.")
                RESULT.E_TEEKM_SEED_EXISTS -> Timber.i("Zion seed has been already created.")
                else -> Timber.e("Zion seed creation result: $result")
            }
        }
    }

    fun signMessage(hexData: String): String {
        val message = JSONObject().apply {
            put("version", "45")
            put("data", hexData)
        }
        val json = JSONObject().apply {
            put("path", "m/44'/60'/0'/0/0")
            put("message", message)
        }
        val signature = ByteArrayHolder()
        uniqueId?.also {
            zkma.signMessage(it, ETHEREUM_TYPE, json.toString(), signature).also { result ->
                if (result != RESULT.SUCCESS) throw RuntimeException("Zion error code: $result")
            }
            return HashUtils.asHex(signature.byteArray)
        }
        throw IllegalStateException("Wallet seed has not been created.")
    }
}