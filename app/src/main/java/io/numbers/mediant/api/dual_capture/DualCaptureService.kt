package io.numbers.mediant.api.dual_capture

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbers.infosnapshot.model.Snapshot
import io.numbers.mediant.BuildConfig
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.getHashFromString
import kotlinx.coroutines.suspendCancellableCoroutine
import org.witness.proofmode.crypto.HashUtils
import org.witness.proofmode.crypto.PgpUtils
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


// TODO: replace Timber.e with throw (handle exception by showing snackbar)
class DualCaptureService @Inject constructor(
    private val application: Application
) {
    private val cap = ImageCapture(createCaptureConfig())
    // data directory is "/data"
    //private val capturedImageDir = Environment.getDataDirectory()
    private val capturedImageDir = application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    private val capturedImage = File(capturedImageDir, "test1.jpg")

    private val sd = Environment.getExternalStorageDirectory()

    private val filename = "test1.jpg"
    private val dest = File(capturedImageDir, filename)
    private var imageCapture: ImageCapture? = null
    private fun createCaptureConfig(): ImageCaptureConfig {
        /* This expression will be broken in >= alpha09
         * https://stackoverflow.com/questions/59938111/
         */
        return ImageCaptureConfig.Builder().build()
    }

    fun takePicture() : ImageCapture {
        bindCamera()
        Timber.d("Will save captured image to ${capturedImageDir?.absolutePath}")
//        cap.takePicture(capturedImage, object: ImageCapture.OnImageSavedListener {
//            override fun onError(
//                imageCaptureError: ImageCapture.ImageCaptureError,
//                message: String,
//                cause: Throwable?
//            ) {
//                Timber.e("$imageCaptureError, $message")
//            }
//
//            override fun onImageSaved(file: File) {
//                Timber.i("Successfully take a picture and save ${file.name} to ${file.absolutePath}")
//            }
//        })

        imageCapture?.takePicture(dest, object : ImageCapture.OnImageSavedListener {
            override fun onError(
                imageCaptureError: ImageCapture.ImageCaptureError,
                message: String,
                cause: Throwable?
            ) {
                Timber.e("$imageCaptureError, $message")
            }
            override fun onImageSaved(file: File) {

                Log.v("Image", "拍照成功 Successfully saved image")
                Log.d("Image", "拍照 Successfully saved image")

            }
        })

    }
    private fun bindCamera(){
        CameraX.unbindAll()

        // Image capture config which controls the Flash and Lens
        val imageCaptureConfig = ImageCaptureConfig.Builder()
//            .setTargetRotation(windowManager.defaultDisplay.rotation)
//            .setLensFacing(lensFacing)
            .setFlashMode(FlashMode.ON)
            .build()

        imageCapture = ImageCapture(imageCaptureConfig)

        // Bind the camera to the lifecycle
//        CameraX.bindToLifecycle(this as LifecycleOwner, imageCapture)
    }

    fun getPreview(): Preview {
        CameraX.unbindAll()

        val lensFacing = CameraX.LensFacing.BACK
        val previewConfig = PreviewConfig.Builder()
            .setLensFacing(lensFacing)
            .build()
//        CameraX.bindToLifecycle(this as LifecycleOwner, cap)

        return Preview(previewConfig)
    }
}
