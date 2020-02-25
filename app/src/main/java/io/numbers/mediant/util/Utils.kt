package io.numbers.mediant.util

import android.graphics.Bitmap
import com.google.protobuf.Timestamp
import io.textile.textile.Util
import org.witness.proofmode.crypto.HashUtils
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

enum class ActivityRequestCodes(val value: Int) { CAPTURE_IMAGE(0), CAPTURE_VIDEO(1) }

fun timestampToString(timestamp: Timestamp): String =
    SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
        Util.timestampToDate(timestamp)
    )

fun File.fromInputStream(inputStream: InputStream) {
    inputStream.use { input ->
        this.outputStream().use { fileOut ->
            input.copyTo(fileOut)
        }
    }
}

fun getHashFromString(string: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val digest = messageDigest.digest(string.toByteArray())
    return HashUtils.asHex(digest)
}

fun rescaleBitmap(bitmap: Bitmap, width: Int = 800): Bitmap {
    val scale = width.toDouble() / bitmap.width
    val newHeight = (bitmap.height * scale).toInt()
    return Bitmap.createScaledBitmap(bitmap, width, newHeight, true)
}
