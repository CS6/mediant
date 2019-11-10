package io.numbers.mediant.model

import com.squareup.moshi.JsonClass
import io.numbers.mediant.api.proofmode.ProofSignatureBundle

@JsonClass(generateAdapter = true)
data class Meta(
    val mediaType: MediaType,
    val proof: String,
    val proofSignature: String,
    val mediaSignature: String,
    val signatureProvider: SignatureProvider
) {
    enum class MediaType { JPG, MP4, UNKNOWN }
    enum class SignatureProvider { PROOFMODE, ZION, UNKNOWN }

    constructor(
        mediaType: MediaType,
        proofSignatureBundle: ProofSignatureBundle
    ) : this(
        mediaType,
        proofSignatureBundle.proof,
        proofSignatureBundle.proofSignature,
        proofSignatureBundle.mediaSignature,
        proofSignatureBundle.signatureProvider
    )
}