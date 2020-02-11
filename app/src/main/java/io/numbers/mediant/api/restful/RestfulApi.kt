package io.numbers.mediant.api.restful

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.*

const val BASE_URL = "http://54.174.111.63/"
//const val BASE_URL = "https://postman-echo.com/"

const val ADDED_CONTENTS = "addedcontents"

interface RestfulApi {

    @GET("api/v1/media/")
    suspend fun getMedia(): String

    //@Headers("Authorization: token 8451082d152e8239324d6c717e5510d744f4cccb")
    @Multipart
    @POST("api/v1/media/")
    suspend fun postMedia(
        @Part("file") file: RequestBody,
        @Part("meta") meta: RequestBody,
        @Header("Authorization") authHeader: String): String

    @Multipart
    @POST("api/v1/media/")
    suspend fun postMediaWithMultipart(
        @Part file: MultipartBody.Part,
        @Part("meta") meta: RequestBody,
        @Header("Authorization") authHeader: String): String

    /*
    @POST("ccapi/ver100/shooting/liveview")
    suspend fun startLiveView(
        @Body body: String = JSONObject().run {
            put("liveviewsize", "medium")
            put("cameradisplay", "on")
            toString()
        }
    ): String
     */

    @GET("get?foo1=bar1&foo2=bar2")
    suspend fun getEcho(): String

    @POST("post")
    suspend fun postEcho(@Body body: String): String

    @Multipart
    @POST("post")
    suspend fun postMultipartEcho(
        @Part file: MultipartBody.Part,
        @Part("meta") meta: RequestBody): String

    @Multipart
    @POST("post")
    suspend fun postMultipart2Echo(
        @Part("file") file: RequestBody,
        @Part("meta") meta: RequestBody): String
}