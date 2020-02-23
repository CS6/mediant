package io.numbers.mediant.api.halasystems

import okhttp3.MultipartBody
import retrofit2.http.*

const val BASE_URL = "https://i1ue5evfe7.execute-api.us-east-1.amazonaws.com/dev/"
const val BASE_URL_ONETIME = "https://verify-silo-uploads-dev.s3.amazonaws.com/"
const val API_KEY= ""

interface HalaSystemsApi {

    @GET("upload/")
    suspend fun getUpload(
        @Header("x-api-key") apiKey: String = API_KEY,
        @Header("x-amz-meta-filekey") fileName: String): String

    @Multipart
    @PUT
    suspend fun putUpload(
        @Url url: String,
        @Part file: MultipartBody.Part): String
}
