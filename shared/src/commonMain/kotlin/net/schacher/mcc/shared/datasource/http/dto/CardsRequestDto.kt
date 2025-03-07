package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardsRequestDto(val cardCodes: List<String>)
