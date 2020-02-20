package io.numbers.mediant.api.sealr

import io.numbers.mediant.model.Meta
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Url
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class SealrService @Inject constructor(private val sealrApi: SealrApi) {
    /*
    val retrofit = initRestfulService()

    private fun initRestfulService(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
    */

    suspend fun getMedia(): String {
        val ret = sealrApi.getMedia()
        Timber.i("Restful getMedia returns $ret")
        return ret
    }

    suspend fun postMedia(file: RequestBody, meta: RequestBody): String {
        val token = "token ee947510026556118e5092f97982f0ac7a81772b"
        Timber.i("Restful postMedia is called")
        val ret = sealrApi.postMedia(file, meta, token)
        Timber.i("Restful postMedia returns $ret")
        return ret
    }

    suspend fun postMediaWithMultipart(file: MultipartBody.Part): String {
        Timber.i("Restful postMediaPartVersion is called")
        val token = "token ee947510026556118e5092f97982f0ac7a81772b"
        var ret: String = ""
        try {
            ret = sealrApi.postMediaWithMultipart(file, token)
            Timber.i("Restful postMediaPartVersion returns $ret")
        } catch (e: Exception) {
            Timber.e("$e")
            Timber.e("Failed to get Sealr API result. Check network connection.")
            ret = ""
        }
        return ret
    }

    suspend fun getEcho(): String {
        val ret = sealrApi.getEcho()
        Timber.i("Restful getEcho returns $ret")
        return ret
    }

    suspend fun postEcho(body: String): String {
        val ret = sealrApi.postEcho(body)
        Timber.i("Restful postEcho returns $ret")
        return ret
    }

    suspend fun postMultipartEcho(file: MultipartBody.Part, meta: RequestBody): String {
        Timber.i("Restful postMultipartEcho is called")
        val ret = sealrApi.postMultipartEcho(file, meta)
        Timber.i("Restful postMultipartEcho returns $ret")
        return ret
    }

    suspend fun postMultipart2Echo(file: RequestBody, meta: RequestBody): String {
        Timber.i("Restful postMultipart2Echo is called")
        val ret = sealrApi.postMultipart2Echo(file, meta)
        Timber.i("Restful postMultipart2Echo returns $ret")
        return ret
    }
}