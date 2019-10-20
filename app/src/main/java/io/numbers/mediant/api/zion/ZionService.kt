package io.numbers.mediant.api.zion

import android.app.Application
import org.witness.proofmode.util.DeviceInfo
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ZionService @Inject constructor(
    private val application: Application
) {

    init {
        Timber.d(
            DeviceInfo.getDeviceInfo(
                application.applicationContext,
                DeviceInfo.Device.DEVICE_HARDWARE_MODEL
            )
        )
    }

    val deviceName: String
        get() = DeviceInfo.getDeviceInfo(
            application.applicationContext,
            DeviceInfo.Device.DEVICE_HARDWARE_MODEL
        ).toUpperCase(Locale.ENGLISH)

    val isHtcExodus1: Boolean
        get() = "HTC EXODUS 1" == deviceName
}