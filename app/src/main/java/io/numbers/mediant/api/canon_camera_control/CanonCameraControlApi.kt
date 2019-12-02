package io.numbers.mediant.api.canon_camera_control

import retrofit2.http.GET

const val BASE_URL = "http://192.168.1.2:8080/"

interface CanonCameraControlApi {

    @GET("ccapi")
    suspend fun connect(): String

    @GET("ccapi/ver100/event/polling")
    suspend fun poll(): String
}