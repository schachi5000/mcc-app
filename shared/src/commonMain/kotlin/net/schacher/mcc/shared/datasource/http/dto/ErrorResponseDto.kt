package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val error: String,
    val message: String?
)