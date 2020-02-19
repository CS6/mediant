package io.numbers.mediant.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SealrVerification(
    val type: String,
    val result: String,
    val confidence: Float
)

@JsonClass(generateAdapter = true)
data class SealrModel(
    val id: Int,
    val uploaded_at: String,
    val provider: String,
    val overall_verification: String,
    val verifications: List<SealrVerification>
)
