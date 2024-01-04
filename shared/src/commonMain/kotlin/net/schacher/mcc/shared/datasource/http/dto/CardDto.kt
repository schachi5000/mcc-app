package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val attack: Int?,
    val base_threat_fixed: Boolean,
    val card_set_code: String?,
    val card_set_name: String?,
    val card_set_type_name_code: String?,
    val code: String,
    val deck_limit: Int?,
    val defense: Int?,
    val double_sided: Boolean,
    val escalation_threat_fixed: Boolean,
    val faction_code: String,
    val faction_name: String,
    val flavor: String?,
    val hand_size: Int?,
    val cost: Int?,
    val health: Int?,
    val health_per_hero: Boolean,
    val hidden: Boolean,
    val imagesrc: String?,
    val is_unique: Boolean,
    val linked_card: CardDto?,
    val linked_to_code: String?,
    val linked_to_name: String?,
    val meta: Meta?,
    val name: String,
    val octgn_id: String?,
    val pack_code: String,
    val pack_name: String,
    val permanent: Boolean,
    val position: Int,
    val quantity: Int,
    val real_name: String?,
    val real_text: String?,
    val real_traits: String?,
    val text: String?,
    val boost_text: String?,
    val threat_fixed: Boolean,
    val thwart: Int?,
    val traits: String?,
    val type_code: String?,
    val type_name: String?,
    val url: String
)

@Serializable
data class Meta(
    val colors: List<String>,
    val offset: String?
)