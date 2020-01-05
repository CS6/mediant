package io.numbers.mediant.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.numbers.mediant.R

class PermissionManager(private val context: Context) {

    fun hasPermissions(permissionRequestType: PermissionRequestType) =
        permissionRequestType.value.permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    fun askPermissions(permissionRequestType: PermissionRequestType, fragment: Fragment): Boolean {
        permissionRequestType.value.permissions.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.requireActivity(), it
                )
            ) return false
        }
        fragment.requestPermissions(
            permissionRequestType.value.permissions,
            permissionRequestType.value.code
        )
        return true
    }
}

enum class PermissionRequestType(val value: PermissionRequest) {
    PROOFMODE_IMAGE(
        PermissionRequest(
            0,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            R.string.message_proofmode_permissions_rationale
        )
    ),
    PROOFMODE_VIDEO(
        PermissionRequest(
            1,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            R.string.message_proofmode_permissions_rationale
        )
    )
}

data class PermissionRequest(
    val code: Int,
    val permissions: Array<String>,
    @StringRes val rationale: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionRequest

        if (code != other.code) return false
        if (!permissions.contentEquals(other.permissions)) return false
        if (rationale != other.rationale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + permissions.contentHashCode()
        result = 31 * result + rationale
        return result
    }
}