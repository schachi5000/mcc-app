package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class PackDto(
    val name: String,
    val code: String,
    val position: Int,
    val id: Int
)
