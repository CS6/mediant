package io.numbers.mediant.api.canon_camera_control

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

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
}