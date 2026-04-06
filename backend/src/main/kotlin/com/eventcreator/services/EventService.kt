package com.eventcreator.services

import com.eventcreator.dto.EventRequest
import com.eventcreator.dto.EventResponse
import com.eventcreator.dto.validate
import com.eventcreator.repositories.EventRepository
import java.util.UUID

class ValidationException(val errors: List<String>) : Exception(errors.joinToString(", "))
class NotFoundException(message: String) : Exception(message)

class EventService(private val repository: EventRepository = EventRepository()) {

    fun listEvents(
        search: String?,
        category: String?,
        status: String?,
        sort: String?
    ): List<EventResponse> = repository.findAll(search, category, status, sort)

    fun getEvent(id: String): EventResponse {
        val uuid = parseUuid(id)
        return repository.findById(uuid) ?: throw NotFoundException("Evento con id=$id no encontrado")
    }

    fun createEvent(req: EventRequest): EventResponse {
        val errors = req.validate()
        if (errors.isNotEmpty()) throw ValidationException(errors)
        return repository.create(req)
    }

    fun updateEvent(id: String, req: EventRequest): EventResponse {
        val errors = req.validate()
        if (errors.isNotEmpty()) throw ValidationException(errors)
        val uuid = parseUuid(id)
        return repository.update(uuid, req) ?: throw NotFoundException("Evento con id=$id no encontrado")
    }

    fun deleteEvent(id: String): Boolean {
        val uuid = parseUuid(id)
        return repository.delete(uuid).also {
            if (!it) throw NotFoundException("Evento con id=$id no encontrado")
        }
    }

    private fun parseUuid(id: String): UUID =
        runCatching { UUID.fromString(id) }.getOrElse {
            throw ValidationException(listOf("El id '$id' no es un UUID válido"))
        }
}
