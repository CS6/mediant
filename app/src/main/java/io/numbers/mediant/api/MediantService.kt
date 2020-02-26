package io.numbers.mediant.api

import com.squareup.moshi.Moshi
import io.numbers.mediant.api.halasystems.HalaSystemsService
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.api.sealr.SealrService
import io.numbers.mediant.api.restful.RestfulService
import io.numbers.mediant.api.session_based_signature.SessionBasedSignatureService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.model.MetaJsonAdapter
import io.numbers.mediant.model.SealrModel
import io.numbers.mediant.model.SealrModelJsonAdapter
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.getHashFromString
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.witness.proofmode.crypto.HashUtils
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MediantService @Inject constructor(
    private val textileService: TextileService,
    private val proofModeService: ProofModeService,
    private val sessionBasedSignatureService: SessionBasedSignatureService,
    private val zionService: ZionService,
    private val restfulService: RestfulService,
    private val sealrService: SealrService,
    private val halaSystemsService: HalaSystemsService,
    private val preferenceHelper: PreferenceHelper,
    private val moshi: Moshi
) {

    /**
     * Upload image and meta JSON (aka ProofSignatureBundle)
     * to Textile Thread v1.
     */
    suspend fun uploadImage(file: File, currentOutputFolder: File) {
        /*
         * Environment.DIRECTORY_DOCUMENTS
         * <storage>/Android/data/io.numbers/mediant/files/Documents/<timestamp>/
         */
        val metaJson = generateMetaJson(file.absolutePath, Meta.MediaType.JPG)
        //writeMeta(metaJson, currentOutputFolder)
        writeMeta(metaJson, File(file.parent))
        textileService.addFile(file.absolutePath, metaJson)
        Timber.i("Image path: ${file.absolutePath}")
        //Timber.i("MetaJSON path: $currentOutputFolder")
        Timber.i("MetaJSON path: ${file.parent}/meta.json")
    }

    suspend fun uploadVideo(file: File, currentOutputFolder: File) {
        val metaJson = generateMetaJson(file.absolutePath, Meta.MediaType.MP4)
        writeMeta(metaJson, currentOutputFolder)
        textileService.addFile(file.absolutePath, metaJson)
    }

    private fun writeMeta(metaJson: String, currentOutputFolder: File) {
        val outputFile = currentOutputFolder.resolve("meta.json")
        outputFile.writeText(metaJson)
        Timber.i("Write meta to file: $outputFile")
    }

    private suspend fun generateMetaJson(filePath: String, mediaType: Meta.MediaType): String {
        val proofSignatureBundle = if (preferenceHelper.signWithZion) {
            //generateProofWithZion(filePath)
            sessionBasedSignatureService.startSession(preferenceHelper.sessionBasedSignatureDuration.toLong() * 60 * 1000)
            sessionBasedSignatureService.generateProofAndSignatures(filePath)
        } else proofModeService.generateProofAndSignatures(filePath)

        saveProofSignatureBundle(proofSignatureBundle , File(filePath).parent)

        return MetaJsonAdapter(moshi).toJson(Meta(mediaType, proofSignatureBundle))
    }

    private fun saveProofSignatureBundle(bundle: ProofSignatureBundle,
                                         outputFolderPath: String) {
        val outputDir: String = outputFolderPath
        Timber.i("Save ProofSignatureBundle to $outputDir")
        File("$outputDir/proof.txt").writeText(bundle.proof)
        File("$outputDir/proofSignature.txt").writeText(bundle.proofSignature)
        File("$outputDir/mediaSignature.txt").writeText(bundle.mediaSignature)
        File("$outputDir/sessionKey.pub").writeText(sessionBasedSignatureService.publicKey)
        File("$outputDir/sessionKeySigned.pub").writeText(sessionBasedSignatureService.signedPublicKey)
    }

    /**
     * (DEPRECATED) Generate ProofSignatureBundle signed by Zion.
     *
     * When you take a photo, Zion will popup twice for signature
     * (for image and proof).
     *
     * This is suitable for demo, but is a bad UX in real scenarios.
     */
    private suspend fun generateProofWithZion(filePath: String): ProofSignatureBundle {
        val proof = proofModeService.generateProofAndSignatures(filePath).proof
        val mediaHash = HashUtils.getSHA256FromFileContent(File(filePath))
        val proofHash = getHashFromString(proof)
        return ProofSignatureBundle(
            proof,
            zionService.signMessage(proofHash),
            zionService.signMessage(mediaHash),
            Meta.SignatureProvider.ZION
        )
    }

    fun createMediaFile(root: File, fileName: String): File {
        val currentOutputFolder = createCurrentOutputFolder(root)
        return File(currentOutputFolder, fileName).apply {
            Timber.i("New image will be saved to: $absolutePath")
        }
    }

    private fun createCurrentOutputFolder(root: File): File {
        val currentOutputFolder = root.resolve("${System.currentTimeMillis()}")
        if (!currentOutputFolder.exists()) currentOutputFolder.mkdir()
        return currentOutputFolder
    }

    suspend fun uploadImageToSealr(file: File): String {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return sealrService.postMediaWithMultipart(body)
    }

    suspend fun uploadToHala(file: File): String {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val body = MultipartBody.Part.create(requestFile)
        return halaSystemsService.upload(body)
    }

    suspend fun uploadEchoByRestful() {
        restfulService.getEcho()

        val body = JSONObject().run {
            put("foo1", "bar1")
            put("foo2", "bar2")
            toString()
        }
        restfulService.postEcho(body)
    }

    suspend fun uploadImageByRestful(file: File) {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val metaJson = generateMetaJson(file.absolutePath, Meta.MediaType.JPG)
        val meta = RequestBody.create(MediaType.parse("multipart/form-data"), metaJson.toString())

        // MultipartBody.Part Version
        //restfulService.postMultipartEcho(body, meta)
        restfulService.postMediaWithMultipart(body, meta)

        // RequestBody Version
        //val body2 = RequestBody.create(MediaType.parse("image/*"), file)
        //restfulService.postMedia(body2, meta)

        //val body3 = RequestBody.create(MediaType.parse("multipart/octet-stream"), file.readBytes())

        //restfulService.postMultipart2Echo(body2, meta)
        //restfulService.postMedia(body2, meta)
        //restfulService.postMedia(body3, meta)
    }
}