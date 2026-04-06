package com.eventcreator.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object Events : Table("events") {
    val id          = uuid("id").autoGenerate()
    val title       = varchar("title", 255)
    val description = text("description").nullable()
    val eventDate   = date("event_date")
    val eventTime   = time("event_time")
    val location    = varchar("location", 255)
    val category    = varchar("category", 100)
    val capacity    = integer("capacity")
    val price       = decimal("price", 10, 2)
    val organizer   = varchar("organizer", 255)
    val status      = varchar("status", 20)
    val createdAt   = timestampWithTimeZone("created_at")
    val updatedAt   = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(id)
}
