package io.numbers.mediant.api.canon_camera_control

import kotlinx.coroutines.delay
import org.json.JSONObject
import javax.inject.Inject

class CanonCameraControlService @Inject constructor(private val canonCameraControlApi: CanonCameraControlApi) {

    suspend fun startPolling(callback: (JSONObject) -> Unit) {
        while (true) {
            callback(JSONObject(canonCameraControlApi.poll()))
            delay(1000)
        }
    }
}