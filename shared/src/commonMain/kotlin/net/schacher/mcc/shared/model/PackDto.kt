package net.schacher.mcc.shared.model

data class Pack(
    val name: String,
    val code: String,
    val cards: List<Card>,
    val url: String
)
