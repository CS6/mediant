package io.dt42.mediant.model

import com.google.protobuf.Timestamp

data class Post(
    val username: String,
    val date: Timestamp,
    val data: ByteArray?,
    val caption: String
)