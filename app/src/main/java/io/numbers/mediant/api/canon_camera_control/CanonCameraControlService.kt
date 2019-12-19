package io.numbers.mediant.api.canon_camera_control

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.http.Url
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class CanonCameraControlService @Inject constructor(private val canonCameraControlApi: CanonCameraControlApi) {

    suspend fun connect() = canonCameraControlApi.connect()

    suspend fun startPolling() = flow {
        while (true) {
            try {
                emit(JSONObject(canonCameraControlApi.poll()))
            } catch (e: HttpException) {
                if (e.code() == 503) Timber.e(e)
                else throw e
            }
            delay(3000)
        }
    }

    suspend fun getContent(@Url url: String): ResponseBody {
        Timber.i("Try to get contents: $url")
        return canonCameraControlApi.getContent(url)
    }

    suspend fun startLiveView(): Flow<InputStream> {
        canonCameraControlApi.startLiveView()
        return flow {
            while (true) {
                try {
                    emit(canonCameraControlApi.retrieveLiveView().byteStream())
                } catch (e: HttpException) {
                    if (e.code() == 503) Timber.e(e)
                    else throw e
                }
                delay(300)
            }
        }
    }
}