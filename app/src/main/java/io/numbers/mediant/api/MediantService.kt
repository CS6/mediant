package io.numbers.mediant.api

import com.squareup.moshi.Moshi
import io.numbers.mediant.api.proofmode.ProofModeService
import io.numbers.mediant.api.proofmode.ProofSignatureBundle
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
    private val zionService: ZionService,
    private val preferenceHelper: PreferenceHelper,
    private val moshi: Moshi
) {

    lateinit var currentOutputFolder: File
    private lateinit var currentMediaPath: String

    suspend fun uploadImage() {
        val metaJson = generateMetaJson(currentMediaPath, Meta.MediaType.JPG)
        writeMeta(metaJson)
        textileService.addFile(currentMediaPath, metaJson)
    }

    suspend fun uploadVideo() {
        val metaJson = generateMetaJson(currentMediaPath, Meta.MediaType.MP4)
        writeMeta(metaJson)
        textileService.addFile(currentMediaPath, metaJson)
    }

    private fun writeMeta(metaJson: String) {
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
        createCurrentOutputFolder(root)
        return File(currentOutputFolder, fileName).apply {
            currentMediaPath = absolutePath
            Timber.i("New image will be saved to: $currentMediaPath")
        }
    }

    private fun createCurrentOutputFolder(root: File) {
        currentOutputFolder = root.resolve("${System.currentTimeMillis()}")
        if (!currentOutputFolder.exists()) currentOutputFolder.mkdir()
    }
}