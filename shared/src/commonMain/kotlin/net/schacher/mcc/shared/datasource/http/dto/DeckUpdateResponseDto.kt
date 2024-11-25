package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class DeckUpdateResponseDto(val success: Boolean, val msg: JsonElement?)
