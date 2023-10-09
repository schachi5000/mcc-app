package model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val code: String,
    val name: String
)

