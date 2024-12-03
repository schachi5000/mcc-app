package net.schacher.mcc.shared.datasource.http.dto

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val error: String,
    val code: Int,
    val message: String
) {
    constructor(httpStatusCode: HttpStatusCode, message: String) :
            this(
                httpStatusCode.description,
                httpStatusCode.value,
                message
            )
}