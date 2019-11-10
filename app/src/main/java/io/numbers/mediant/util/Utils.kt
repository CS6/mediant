package io.numbers.mediant.util

import com.google.protobuf.Timestamp
import io.textile.textile.Util
import org.witness.proofmode.crypto.HashUtils
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

enum class ActivityRequestCodes(val value: Int) { CAPTURE_IMAGE(0), CAPTURE_VIDEO(1) }

fun timestampToString(timestamp: Timestamp): String =
    SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
        Util.timestampToDate(timestamp)
    )

fun File.deleteDirectory(): Boolean {
    return if (exists()) {
        listFiles()?.forEach {
            if (it.isDirectory) it.deleteDirectory()
            else it.delete()
        }
        delete()
    } else false
}

fun getHashFromString(string: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val digest = messageDigest.digest(string.toByteArray())
    return HashUtils.asHex(digest)
}