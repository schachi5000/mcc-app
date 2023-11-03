package net.schacher.mcc.shared.datasource.http

import kotlinx.serialization.Serializable

@Serializable
data class PackDto(
    val name: String,
    val code: String,
    val position: Int,
    val id: Int,
    val known: Int,
    val total: Int,
    val url: String
)
