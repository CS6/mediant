package io.numbers.mediant.api.restful

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Url
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class RestfulService @Inject constructor(private val restfulApi: RestfulApi) {
    /*
    val retrofit = initRestfulService()

    private fun initRestfulService(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
    */

    private val apiToken = ""

    private fun createDebugApi(): RestfulApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(RestfulApi::class.java)
    }

    suspend fun getMedia(): String {
        val ret = restfulApi.getMedia()
        Timber.i("Restful getMedia returns $ret")
        return ret
    }

    suspend fun postMedia(file: RequestBody, meta: RequestBody): String {
        Timber.i("Restful postMedia is called")
        val ret = restfulApi.postMedia(file, meta, apiToken)
        Timber.i("Restful postMedia returns $ret")
        return ret
    }

    suspend fun postMediaWithMultipart(file: MultipartBody.Part, meta: RequestBody): String {
        Timber.i("Restful postMediaPartVersion is called")
        val ret = restfulApi.postMediaWithMultipart(file,
                                                    meta,
                                                    "Hala Systems",
                                                    apiToken)
        Timber.i("Restful postMediaPartVersion returns $ret")
        return ret
    }

    suspend fun postMediaWithMultipartDebug(file: MultipartBody.Part, meta: RequestBody): String {
        Timber.i("Debugging RESTful POST is called")
        val debuggingRestfulApi = createDebugApi()
        val ret = debuggingRestfulApi.postMediaWithMultipart(file,
            meta,
            "Hala Systems",
            apiToken)
        Timber.i("Debugging RESTful POST returns $ret")
        return ret
    }

    suspend fun getEcho(): String {
        val ret = restfulApi.getEcho()
        Timber.i("Restful getEcho returns $ret")
        return ret
    }

    suspend fun postEcho(body: String): String {
        val ret = restfulApi.postEcho(body)
        Timber.i("Restful postEcho returns $ret")
        return ret
    }

    suspend fun postMultipartEcho(file: MultipartBody.Part, meta: RequestBody): String {
        Timber.i("Restful postMultipartEcho is called")
        val ret = restfulApi.postMultipartEcho(file, meta)
        Timber.i("Restful postMultipartEcho returns $ret")
        return ret
    }

    suspend fun postMultipart2Echo(file: RequestBody, meta: RequestBody): String {
        Timber.i("Restful postMultipart2Echo is called")
        val ret = restfulApi.postMultipart2Echo(file, meta)
        Timber.i("Restful postMultipart2Echo returns $ret")
        return ret
    }
}