package io.numbers.mediant.api.proofmode

import android.app.Application
import com.github.wnameless.json.flattener.JsonFlattener
import io.numbers.infosnapshot.InfoSnapshotBuilder
import io.numbers.mediant.model.Meta
import io.numbers.mediant.util.PreferenceHelper
import org.json.JSONObject
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
        var proof = JSONObject(
            InfoSnapshotBuilder(application.applicationContext)
                .apply { duration = preferenceHelper.infoSnapshotDuration.toLong() * 1000 }
                .snap()
                .toJson()
        ).put("mediaHash", HashUtils.getSHA256FromFileContent(File(filePath))).toString(2)

        // TODO: Use RecycleView for better UI control.
        // Currently, we only flatten JSON for readability (request from Jonathan).
        proof = JSONObject(JsonFlattener.flatten(proof)).toString(0)
            .replace("\"", "")
            .replace("{", "")
            .replace("}", "")
        /////////////////////////////////////////////////////////////


        val mediaSignature = generateMediaSignature(filePath)
        val proofSignature = generateProofSignature(proof)

        return ProofSignatureBundle(
            proof,
            proofSignature,
            mediaSignature,
            Meta.SignatureProvider.PROOFMODE
        )
    }

    private fun generateProofSignature(proof: String): String {
        val proofSignatureStream = ByteArrayOutputStream()
        PgpUtils.getInstance(application.applicationContext).createDetachedSignature(
            ByteArrayInputStream(proof.toByteArray()),
            proofSignatureStream,
            PgpUtils.DEFAULT_PASSWORD
        )
        return String(proofSignatureStream.toByteArray())
    }

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