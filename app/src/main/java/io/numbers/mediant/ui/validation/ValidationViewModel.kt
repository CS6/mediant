package io.numbers.mediant.ui.validation

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.util.rescaleBitmap
import timber.log.Timber
import javax.inject.Inject

class ValidationViewModel @Inject constructor(
    private val application: Application,
    private val textileService: TextileService
) : ViewModel() {

    val fileHash = MutableLiveData("")
    val imageDrawable = MediatorLiveData<Drawable>()
    val userName = MutableLiveData("")
    val blockTimestamp = MutableLiveData("")
    val blockHash = MutableLiveData("")

    val meta = MutableLiveData<Meta>()

    var debug = "Default"

    init {
        imageDrawable.addSource(fileHash) { updateImage(it) }
    }

    private fun updateImage(fileHash: String) {
        textileService.fetchRawContent(fileHash) {
            val bitmap = rescaleBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            imageDrawable.postValue(
                BitmapDrawable(application.resources, bitmap)
            )
        }
    }

    fun onUpload() {
        debug = "onUpload is called"
        Timber.i("$debug")
    }

    fun onResult() {
        debug = "onResult is called"
        Timber.i("$debug")
    }
}