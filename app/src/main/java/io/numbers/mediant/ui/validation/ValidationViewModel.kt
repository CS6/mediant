package io.numbers.mediant.ui.validation

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import io.numbers.mediant.api.MediantService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.model.SealrModelJsonAdapter
import io.numbers.mediant.util.rescaleBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import javax.inject.Inject

class ValidationViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService,
    private val mediantService: MediantService,
    private val moshi: Moshi
) : ViewModel() {

    val fileHash = MutableLiveData("")
    val imageDrawable = MediatorLiveData<Drawable>()
    val userName = MutableLiveData("")
    val blockTimestamp = MutableLiveData("")
    val blockHash = MutableLiveData("")

    val meta = MutableLiveData<Meta>()

    private val tmpFilePath = application.getExternalFilesDir(
                                 Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/tmp.jpg"
    //var debug = "Default"
    var debug = MutableLiveData("Press the upload icon to verify your image.")

    init {
        imageDrawable.addSource(fileHash) {
            updateImage(it)
            saveImage(it)
        }
    }

    private fun updateImage(fileHash: String) {
        textileService.fetchRawContent(fileHash) {
            val bitmap = rescaleBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            imageDrawable.postValue(
                BitmapDrawable(application.resources, bitmap)
            )
        }
    }

    private fun saveImage(fileHash: String) {
        textileService.fetchRawContent(fileHash) {
            /*
            val bitmap = rescaleBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            imageDrawable.postValue(
                BitmapDrawable(application.resources, bitmap)
            )
             */

            //val f = File(application.getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS) + "/tmp.jpg")
            val stream = File(tmpFilePath)
            stream.writeBytes(it)
            Timber.i("Write tmp file to ${stream.absolutePath}")
        }
    }

    fun onUpload() {
        debug.postValue("Verifying your image, please wait for 10 seconds.")
        Timber.i("$debug")

        viewModelScope.launch(Dispatchers.IO) {
            val res = mediantService.uploadImageToSealr(File(tmpFilePath))
            if (res.isNotEmpty()) {
                val sealrModel = SealrModelJsonAdapter(moshi).fromJson(res)
                val fullResult = """
                Overall Verification: ${sealrModel!!.overall_verification}

                ${sealrModel!!.verifications[0].type}
                - result: ${sealrModel!!.verifications[0].result}
                - confidence: ${sealrModel!!.verifications[0].confidence}

                ${sealrModel!!.verifications[1].type}
                - result: ${sealrModel!!.verifications[1].result}
                - confidence: ${sealrModel!!.verifications[1].confidence}

                ${sealrModel!!.verifications[2].type}
                - result: ${sealrModel!!.verifications[2].result}
                - confidence: ${sealrModel!!.verifications[2].confidence}
                """.trimIndent()
                //debug.postValue(sealrModel!!.overall_verification)
                debug.postValue(fullResult)
                Timber.i("JSON result: $sealrModel")
                Timber.i("overall_verification: $debug")
            } else {
                val fullResult = "Failed to run Sealr verification. Please check your network connection."
                debug.postValue(fullResult)
            }
        }
    }

    fun onResult() {
        debug.postValue("onResult is called")
        Timber.i("$debug")
    }
}