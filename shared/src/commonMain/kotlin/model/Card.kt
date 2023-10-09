package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val code: String,
    @SerialName("type_code")
    val type: String,
    val name: String
)

