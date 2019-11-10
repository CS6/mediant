package io.numbers.mediant.api.textile

enum class Properties(val value: String) {
    MEDIA("media"),
    TYPE("type"),
    PROOF("proof"),
    MEDIA_SIGNATURE("media_signature"),
    PROOF_SIGNATURE("proof_signature"),
    SIGNATURE_PROVIDER("signature_provider")
}

enum class MediaType(val value: String) {
    JPG("jpg"),
    MP4("mp4");

    companion object {
        private val map = values().associateBy(MediaType::value)
        operator fun get(value: String) = map[value]
    }
}

enum class SignatureProvider(val value: String) {
    PROOFMODE("proofmode"),
    ZION("zion");

    companion object {
        private val map = values().associateBy(SignatureProvider::value)
        operator fun get(value: String) = map[value]
    }
}