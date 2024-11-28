package net.schacher.mcc.shared.datasource.http.dto

import kotlinx.serialization.Serializable
import net.schacher.mcc.shared.model.Aspect

@Serializable
data class DeckDto(
    val createdOn: String?,
    val updatedOn: String?,
    val description: String?,
    val id: Int,
    val heroCode: String?,
    val heroName: String?,
    val aspect: String?,
    val name: String,
    val slots: Map<String, Int>?,
    val tags: String?,
    val userId: Int?,
    val version: String?,
    val problem: String?
)