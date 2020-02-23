package io.numbers.mediant.api.halasystems

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber

class HalaSystemsService() {
    private val halaSystemsApi: HalaSystemsApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(HalaSystemsApi::class.java)

    private fun createOnetimeApi(url: String): HalaSystemsApi {
        /* Add `.client(client)` below baseUrl to show
         * HTTP request header and body in log.
         */
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL_ONETIME)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(HalaSystemsApi::class.java)
    }

    suspend fun upload(file: MultipartBody.Part): String {
        val url = getUpload()
        Timber.d("Get one-time URL $url")
        return putUpload(url, file)
    }

    private suspend fun getUpload(): String {
        Timber.d("Hala getUpload is called")
        var ret: String = ""
        try {
            ret = halaSystemsApi.getUpload(fileName="image.jpg")
                                .replace("\"", "")
            Timber.d("Hala getUpload returns $ret")
        } catch (e: Exception) {
            Timber.e("$e")
            Timber.e("Failed to get Hala getUpload result. Check network connection.")
            ret = ""
        }
        return ret
    }

    private suspend fun putUpload(url: String, file: MultipartBody.Part): String {
        Timber.d("Hala postUpload is called")
        var ret: String = ""
        try {
            val onetimeApi = createOnetimeApi(url)
            val tailUrl = url.split(".com")[1]
            Timber.d("Tail URL: $tailUrl")
            ret = onetimeApi.putUpload(tailUrl, file)
            Timber.d("Hala postUpload returns $ret")
        } catch (e: Exception) {
            Timber.e("$e")
            Timber.e("Failed to get Hala putUpload result. Check network connection.")
            ret = ""
        }
        return ret
    }
}