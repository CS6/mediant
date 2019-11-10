package io.numbers.mediant.api.proofmode

import io.numbers.mediant.model.Meta

data class ProofSignatureBundle(
    val proof: String,
    val proofSignature: String,
    val mediaSignature: String,
    val signatureProvider: Meta.SignatureProvider
)