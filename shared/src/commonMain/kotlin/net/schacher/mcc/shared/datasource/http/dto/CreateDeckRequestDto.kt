package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateDeckRequestDto(val heroCardCode: String, val deckName: String?)