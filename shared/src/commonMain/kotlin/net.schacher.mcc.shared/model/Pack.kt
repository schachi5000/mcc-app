package net.schacher.mcc.shared.model

import kotlinx.serialization.Serializable


@Serializable
data class Pack(
    val name: String,
    val code: String,
    val position: Int,
    val id: Int,
    val known: Int,
    val total: Int,
    val url: String
)
