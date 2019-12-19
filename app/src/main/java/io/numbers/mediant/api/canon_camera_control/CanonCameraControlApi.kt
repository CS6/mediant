package io.numbers.mediant.api.canon_camera_control

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.*

const val BASE_URL = "http://192.168.1.2:8080/"

const val ADDED_CONTENTS = "addedcontents"

interface CanonCameraControlApi {

    @GET("ccapi")
    suspend fun connect(): String

    @GET("ccapi/ver100/event/polling")
    suspend fun poll(): String

    @GET
    @Streaming
    suspend fun getContent(@Url url: String): ResponseBody

    @POST("ccapi/ver100/shooting/liveview")
    suspend fun startLiveView(
        @Body body: String = JSONObject().run {
            put("liveviewsize", "medium")
            put("cameradisplay", "on")
            toString()
        }
    ): String

    @GET("ccapi/ver100/shooting/liveview/flip")
    @Streaming
    suspend fun retrieveLiveView(): ResponseBody
}