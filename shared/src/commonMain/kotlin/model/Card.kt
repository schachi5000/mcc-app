package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val code: String,
    val position: Int,
    @SerialName("type_code")
    val type: String,
    val name: String,
    @SerialName("imagesrc")
    val imageSrc: String,
    @SerialName("linked_card")
    val linkedCard: Card? = null,
)

