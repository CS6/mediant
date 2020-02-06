package io.numbers.mediant.api.session_based_signature

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.numbers.mediant.BuildConfig
import io.numbers.mediant.util.PreferenceHelper
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


// TODO: replace Timber.e with throw (handle exception by showing snackbar)
class SessionBasedSignatureService @Inject constructor(
    private val application: Application,
    private val preferenceHelper: PreferenceHelper,
    private val sessionBasedSignaturePgp: SessionBasedSignaturePgp
) {
    private var isValidSession: Boolean = false
    private var pgpInstance = sessionBasedSignaturePgp.getInstance(
                                  application.applicationContext)
    private val defaultDuration: Long = 10000  // milliseconds

    /**
     * If current session is valid, returns the existing PGP instance;
     * otherwise, create a new valid session, and returns the newly created
     * PGP instance.
     */
    fun getPgpInstance(duration: Long = defaultDuration): SessionBasedSignaturePgp {
        if (isValidSession) {
            Timber.d("Valid session, use existing PGP instance.")
        } else {
            Timber.d("Invalid session, create new PGP instance.")
            startSession(duration)
            pgpInstance = sessionBasedSignaturePgp.getInstance(application.applicationContext)
        }
        return pgpInstance
    }

    /*
    fun getSessionStatus(): Boolean {
        return isValidSession
    }
    */

    private fun startSession(duration: Long = defaultDuration) {
        val timer = object: CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.i("Timer will expire in ${millisUntilFinished / 1000} secs, session status: $isValidSession")
            }
            override fun onFinish() {
                isValidSession = false
                Timber.i("Timer is finished, session status: $isValidSession")
            }
        }
        isValidSession = true
        timer.start()
    }
}
