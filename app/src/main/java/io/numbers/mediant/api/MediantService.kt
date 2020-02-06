package io.numbers.mediant.api

import com.squareup.moshi.Moshi
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
import io.numbers.mediant.api.session_based_signature.SessionBasedSignatureService
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.zion.ZionService
import io.numbers.mediant.model.Meta
import io.numbers.mediant.model.MetaJsonAdapter
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.util.getHashFromString
import org.witness.proofmode.crypto.HashUtils
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MediantService @Inject constructor(
    private val textileService: TextileService,
    private val proofModeService: ProofModeService,
    private val sessionBasedSignatureService: SessionBasedSignatureService,
    private val zionService: ZionService,
    private val preferenceHelper: PreferenceHelper,
    private val moshi: Moshi
) {

    suspend fun uploadImage(file: File, currentOutputFolder: File) {
        val metaJson = generateMetaJson(file.absolutePath, Meta.MediaType.JPG)
        writeMeta(metaJson, currentOutputFolder)
        textileService.addFile(file.absolutePath, metaJson)
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
            generateProofWithZion(filePath)
        } else proofModeService.generateProofAndSignatures(filePath)
        return MetaJsonAdapter(moshi).toJson(Meta(mediaType, proofSignatureBundle))
    }

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
}