package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val attack: Int?,
    val baseThreatFixed: Boolean,
    val cardSetCode: String?,
    val cardSetName: String?,
    val cardSetTypeNameCode: String?,
    val code: String,
    val deckLimit: Int?,
    val defense: Int?,
    val doubleSided: Boolean,
    val escalationThreatFixed: Boolean,
    val factionCode: String,
    val factionName: String,
    val flavor: String?,
    val handSize: Int?,
    val cost: Int?,
    val health: Int?,
    val healthPerHero: Boolean,
    val hidden: Boolean,
    val imageSrc: String?,
    val unique: Boolean,
    val linkedCard: CardDto?,
    val linkedCardCode: String?,
    val linkedCardName: String?,
    val meta: Meta?,
    val name: String,
    val octagonId: String?,
    val packCode: String,
    val packName: String,
    val permanent: Boolean,
    val position: Int,
    val quantity: Int,
    val realName: String?,
    val realText: String?,
    val realTraits: String?,
    val text: String?,
    val boostText: String?,
    val attackText: String?,
    val threatFixed: Boolean,
    val thwart: Int?,
    val traits: String?,
    val typeCode: String?,
    val typeName: String?,
    val url: String
)

@Serializable
data class Meta(
    val colors: List<String>,
    val offset: String?
)