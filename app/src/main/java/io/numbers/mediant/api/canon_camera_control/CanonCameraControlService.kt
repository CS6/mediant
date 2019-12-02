package io.numbers.mediant.api.canon_camera_control

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.http.Url
import javax.inject.Inject

class CanonCameraControlService @Inject constructor(private val canonCameraControlApi: CanonCameraControlApi) {

    suspend fun connect() = canonCameraControlApi.connect()

    suspend fun startPolling() = flow {
        while (true) {
            emit(JSONObject(canonCameraControlApi.poll()))
            delay(1000)
        }
    }

    suspend fun getContent(@Url url: String) = canonCameraControlApi.getContent(url)
}