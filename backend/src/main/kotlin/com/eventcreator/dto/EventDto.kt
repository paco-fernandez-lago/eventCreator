package com.eventcreator.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val title: String,
    val description: String? = null,
    val eventDate: String,   // ISO format: "2026-05-15"
    val eventTime: String,   // format "HH:mm" or "HH:mm:ss"
    val location: String,
    val category: String,
    val capacity: Int,
    val price: Double,
    val organizer: String,
    val status: String = "draft"
)

@Serializable
data class EventResponse(
    val id: String,
    val title: String,
    val description: String?,
    val eventDate: String,
    val eventTime: String,
    val location: String,
    val category: String,
    val capacity: Int,
    val price: Double,
    val organizer: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ErrorResponse(val error: String)

val VALID_STATUSES = setOf("draft", "published", "cancelled")

fun EventRequest.validate(): List<String> {
    val errors = mutableListOf<String>()
    if (title.isBlank())     errors += "El título es obligatorio"
    if (location.isBlank())  errors += "La ubicación es obligatoria"
    if (organizer.isBlank()) errors += "El organizador es obligatorio"
    if (category.isBlank())  errors += "La categoría es obligatoria"
    if (eventDate.isBlank()) errors += "La fecha es obligatoria"
    if (eventTime.isBlank()) errors += "La hora es obligatoria"
    if (capacity < 0)        errors += "La capacidad no puede ser negativa"
    if (price < 0)           errors += "El precio no puede ser negativo"
    if (status !in VALID_STATUSES)
        errors += "El estado debe ser uno de: draft, published, cancelled"
    return errors
}
