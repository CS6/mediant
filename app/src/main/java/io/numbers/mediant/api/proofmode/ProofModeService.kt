package io.numbers.mediant.api.proofmode

import android.app.Application
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbers.infosnapshot.model.Snapshot
import io.numbers.mediant.model.Meta
import io.numbers.mediant.util.PreferenceHelper
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.witness.proofmode.crypto.HashUtils
import org.witness.proofmode.crypto.PgpUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.Security
import javax.inject.Inject

// TODO: catch throws by showing error message on snackbar
class ProofModeService @Inject constructor(
    private val application: Application,
    private val preferenceHelper: PreferenceHelper
) {

    suspend fun generateProofAndSignatures(filePath: String): ProofSignatureBundle {
        val info = InfoSnapshotBuilder(application.applicationContext)
            .apply { duration = preferenceHelper.infoSnapshotDuration.toLong() * 1000 }
            .snap()

        // TODO: Use RecycleView for better UI control.
        // Currently, we hard code the metadata string for readability (request from Jonathan).
        val metadata = hardCodeMetadata(filePath, info)
        /////////////////////////////////////////////////////////////


        val mediaSignature = generateMediaSignature(filePath)
        val proofSignature = generateProofSignature(metadata)

        return ProofSignatureBundle(
            metadata,
            proofSignature,
            mediaSignature,
            Meta.SignatureProvider.PROOFMODE
        )
    }

    private fun hardCodeMetadata(filePath: String, info: Snapshot): String {
        return """
            Media Hash: ${HashUtils.getSHA256FromFileContent(File(filePath))}
            
            Device Info
            - board: ${info.deviceInfo.value?.board}
            - bootloader: ${info.deviceInfo.value?.bootloader}
            - brand: ${info.deviceInfo.value?.brand}
            - device: ${info.deviceInfo.value?.device}
            - display: ${info.deviceInfo.value?.display}
            - fingerprint: ${info.deviceInfo.value?.fingerprint}
            - hardware: ${info.deviceInfo.value?.hardware}
            - host: ${info.deviceInfo.value?.host}
            - id: ${info.deviceInfo.value?.id}
            - manufacturer: ${info.deviceInfo.value?.manufacturer}
            - model: ${info.deviceInfo.value?.model}
            - product: ${info.deviceInfo.value?.product}
            - supportedAbis: ${info.deviceInfo.value?.supportedAbis}
            - tags: ${info.deviceInfo.value?.tags}
            - buildTime: ${info.deviceInfo.value?.buildTime}
            - type: ${info.deviceInfo.value?.type}
            - user: ${info.deviceInfo.value?.user}
            
            Locale Info
            - country: ${info.localeInfo.value?.country}
            - variant: ${info.localeInfo.value?.variant}
            - language: ${info.localeInfo.value?.language}
            - script: ${info.localeInfo.value?.script}
            - name: ${info.localeInfo.value?.name}
            
            LocationInfo
            - last known
              - accuracy: ${info.locationInfo.value?.lastKnown?.value?.accuracy}
              - altitude: ${info.locationInfo.value?.lastKnown?.value?.altitude}
              - bearing: ${info.locationInfo.value?.lastKnown?.value?.bearing}
              - bearingAccuracyDegrees: ${info.locationInfo.value?.lastKnown?.value?.bearingAccuracyDegrees}
              - latitude: ${info.locationInfo.value?.lastKnown?.value?.latitude}
              - longitude: ${info.locationInfo.value?.lastKnown?.value?.longitude}
              - provider: ${info.locationInfo.value?.lastKnown?.value?.provider}
              - speed: ${info.locationInfo.value?.lastKnown?.value?.speed}
              - speedAccuracyMetersPerSecond: ${info.locationInfo.value?.lastKnown?.value?.speedAccuracyMetersPerSecond}
              - time: ${info.locationInfo.value?.lastKnown?.value?.time}
              - verticalAccuracyMeters: ${info.locationInfo.value?.lastKnown?.value?.verticalAccuracyMeters}
              - isFromMockProvider: ${info.locationInfo.value?.lastKnown?.value?.isFromMockProvider}
              - address: ${info.locationInfo.value?.lastKnown?.value?.address}
            - current
              - accuracy: ${info.locationInfo.value?.current?.value?.accuracy}
              - altitude: ${info.locationInfo.value?.current?.value?.altitude}
              - bearing: ${info.locationInfo.value?.current?.value?.bearing}
              - bearingAccuracyDegrees: ${info.locationInfo.value?.current?.value?.bearingAccuracyDegrees}
              - latitude: ${info.locationInfo.value?.current?.value?.latitude}
              - longitude: ${info.locationInfo.value?.current?.value?.longitude}
              - provider: ${info.locationInfo.value?.current?.value?.provider}
              - speed: ${info.locationInfo.value?.current?.value?.speed}
              - speedAccuracyMetersPerSecond: ${info.locationInfo.value?.current?.value?.speedAccuracyMetersPerSecond}
              - time: ${info.locationInfo.value?.current?.value?.time}
              - verticalAccuracyMeters: ${info.locationInfo.value?.current?.value?.verticalAccuracyMeters}
              - isFromMockProvider: ${info.locationInfo.value?.current?.value?.isFromMockProvider}
              - address: ${info.locationInfo.value?.current?.value?.address}
              
            Sensor Info
             - accelerometer: ${info.sensorInfo.value?.accelerometer}
             - accelerometerUncalibrated: ${info.sensorInfo.value?.accelerometerUncalibrated}
             - ambientTemperature: ${info.sensorInfo.value?.ambientTemperature}
             - gameRotationVector: ${info.sensorInfo.value?.gameRotationVector}
             - geomagneticRotationVector: ${info.sensorInfo.value?.geomagneticRotationVector}
             - gravity: ${info.sensorInfo.value?.gravity}
             - gyroscope: ${info.sensorInfo.value?.gyroscope}
             - gyroscopeUncalibrated: ${info.sensorInfo.value?.gyroscopeUncalibrated}
             - heartBeat: ${info.sensorInfo.value?.heartBeat}
             - heartRate: ${info.sensorInfo.value?.heartRate}
             - light: ${info.sensorInfo.value?.light}
             - linearAcceleration: ${info.sensorInfo.value?.linearAcceleration}
             - lowLatencyOffbodyDetect: ${info.sensorInfo.value?.lowLatencyOffbodyDetect}
             - magneticField: ${info.sensorInfo.value?.magneticField}
             - magneticFieldUncalibrated: ${info.sensorInfo.value?.magneticFieldUncalibrated}
             - motionDetect: ${info.sensorInfo.value?.motionDetect}
             - pose6Dof: ${info.sensorInfo.value?.pose6Dof}
             - pressure: ${info.sensorInfo.value?.pressure}
             - proximity: ${info.sensorInfo.value?.proximity}
             - relativeHumidity: ${info.sensorInfo.value?.relativeHumidity}
             - rotationVector: ${info.sensorInfo.value?.rotationVector}
             - significantMotion: ${info.sensorInfo.value?.significantMotion}
             - stationaryDetect: ${info.sensorInfo.value?.stationaryDetect}
             - stepCounter: ${info.sensorInfo.value?.stepCounter}
             - stepDetector: ${info.sensorInfo.value?.stepDetector}
             
             Settings Info
             - adbEnabled: ${info.settingsInfo.value?.adbEnabled}
             - airplaneModeOn: ${info.settingsInfo.value?.airplaneModeOn}
             - airplaneModeRadios: ${info.settingsInfo.value?.airplaneModeRadios}
             - autoTime: ${info.settingsInfo.value?.autoTime}
             - autoTimeZone: ${info.settingsInfo.value?.autoTimeZone}
             - bluetoothOn: ${info.settingsInfo.value?.bluetoothOn}
             - dataRoaming: ${info.settingsInfo.value?.dataRoaming}
             - developmentSettingsEnabled: ${info.settingsInfo.value?.developmentSettingsEnabled}
             - deviceName: ${info.settingsInfo.value?.deviceName}
             - deviceProvisioned: ${info.settingsInfo.value?.deviceProvisioned}
             - httpProxy: ${info.settingsInfo.value?.httpProxy}
             - modeRinger: ${info.settingsInfo.value?.modeRinger}
             - wifiOn: ${info.settingsInfo.value?.wifiOn}
             - androidIdHash: ${info.settingsInfo.value?.androidIdHash}
        """.trimIndent()
    }

    /**
     * Generate digital signature to stringified proof by SW public key.
     */
    private fun generateProofSignature(proof: String): String {
        val proofSignatureStream = ByteArrayOutputStream()
        PgpUtils.getInstance(application.applicationContext).createDetachedSignature(
            ByteArrayInputStream(proof.toByteArray()),
            proofSignatureStream,
            PgpUtils.DEFAULT_PASSWORD
        )
        return String(proofSignatureStream.toByteArray())
    }

    /**
     * Generate digital signature to file content by SW public key.
     */
    private fun generateMediaSignature(filePath: String): String {
        val mediaSignatureStream = ByteArrayOutputStream()
        PgpUtils.getInstance(application.applicationContext).createDetachedSignature(
            File(filePath).inputStream(),
            mediaSignatureStream,
            PgpUtils.DEFAULT_PASSWORD
        )
        return String(mediaSignatureStream.toByteArray())
    }

    companion object {
        init {
            // Add provider for PgpUtils().createDetachedSignature() from ProofMode
            Security.addProvider(BouncyCastleProvider())
        }
    }
}