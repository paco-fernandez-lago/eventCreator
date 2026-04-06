package com.eventcreator.repositories

import com.eventcreator.dto.EventRequest
import com.eventcreator.dto.EventResponse
import com.eventcreator.models.Events
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class EventRepository {

    fun findAll(
        search: String? = null,
        category: String? = null,
        status: String? = null,
        sort: String? = null
    ): List<EventResponse> = transaction {
        var query = Events.selectAll()

        if (!search.isNullOrBlank()) {
            query = query.andWhere { Events.title.lowerCase() like "%${search.lowercase()}%" }
        }
        if (!category.isNullOrBlank()) {
            query = query.andWhere { Events.category eq category }
        }
        if (!status.isNullOrBlank()) {
            query = query.andWhere { Events.status eq status }
        }

        when (sort) {
            "date_asc"  -> query.orderBy(Events.eventDate to SortOrder.ASC)
            "date_desc" -> query.orderBy(Events.eventDate to SortOrder.DESC)
            else        -> query.orderBy(Events.createdAt to SortOrder.DESC)
        }

        query.map { it.toResponse() }
    }

    fun findById(id: UUID): EventResponse? = transaction {
        Events.selectAll()
            .where { Events.id eq id }
            .singleOrNull()
            ?.toResponse()
    }

    fun create(req: EventRequest): EventResponse = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        // insert{} en Table (no IdTable) devuelve un InsertStatement; sacamos el id del resultado
        val stmt = Events.insert {
            it[title]       = req.title
            it[description] = req.description
            it[eventDate]   = LocalDate.parse(req.eventDate)
            it[eventTime]   = parseTime(req.eventTime)
            it[location]    = req.location
            it[category]    = req.category
            it[capacity]    = req.capacity
            it[price]       = BigDecimal.valueOf(req.price)
            it[organizer]   = req.organizer
            it[status]      = req.status
            it[createdAt]   = now
            it[updatedAt]   = now
        }
        val insertedId = stmt[Events.id]
        Events.selectAll()
            .where { Events.id eq insertedId }
            .single()
            .toResponse()
    }

    fun update(id: UUID, req: EventRequest): EventResponse? = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val updated = Events.update({ Events.id eq id }) {
            it[title]       = req.title
            it[description] = req.description
            it[eventDate]   = LocalDate.parse(req.eventDate)
            it[eventTime]   = parseTime(req.eventTime)
            it[location]    = req.location
            it[category]    = req.category
            it[capacity]    = req.capacity
            it[price]       = BigDecimal.valueOf(req.price)
            it[organizer]   = req.organizer
            it[status]      = req.status
            it[updatedAt]   = now
        }
        if (updated == 0) null
        else Events.selectAll()
            .where { Events.id eq id }
            .single()
            .toResponse()
    }

    fun delete(id: UUID): Boolean = transaction {
        Events.deleteWhere { Events.id eq id } > 0
    }

    // Helpers
    private fun parseTime(value: String): LocalTime =
        if (value.length == 5) LocalTime.parse("$value:00") else LocalTime.parse(value)

    private fun ResultRow.toResponse() = EventResponse(
        id          = this[Events.id].toString(),
        title       = this[Events.title],
        description = this[Events.description],
        eventDate   = this[Events.eventDate].toString(),
        eventTime   = this[Events.eventTime].toString().substring(0, 5), // "HH:mm"
        location    = this[Events.location],
        category    = this[Events.category],
        capacity    = this[Events.capacity],
        price       = this[Events.price].toDouble(),
        organizer   = this[Events.organizer],
        status      = this[Events.status],
        createdAt   = this[Events.createdAt].toString(),
        updatedAt   = this[Events.updatedAt].toString()
    )
}
