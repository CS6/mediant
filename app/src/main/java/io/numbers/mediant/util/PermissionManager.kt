package io.numbers.mediant.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.numbers.mediant.ui.BaseActivity
import javax.inject.Inject

class PermissionManager @Inject constructor(private val baseActivity: BaseActivity) {

    fun hasPermissions(permissionRequestType: PermissionRequestType) =
        permissionRequestType.value.permissions.all {
            ContextCompat.checkSelfPermission(baseActivity, it) == PackageManager.PERMISSION_GRANTED
        }

    fun askPermissions(permissionRequestType: PermissionRequestType): Boolean {
        permissionRequestType.value.permissions.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, it)) return false
        }
        ActivityCompat.requestPermissions(
            baseActivity,
            permissionRequestType.value.permissions,
            permissionRequestType.value.code
        )
        return true
    }
}

enum class PermissionRequestType(val value: PermissionRequest) {
    PROOFMODE(
        PermissionRequest(
            0, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    )
}

data class PermissionRequest(val code: Int, val permissions: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionRequest

        if (code != other.code) return false
        if (!permissions.contentEquals(other.permissions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + permissions.contentHashCode()
        return result
    }
}