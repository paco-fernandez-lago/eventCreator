package com.eventcreator

import com.eventcreator.dto.ErrorResponse
import com.eventcreator.dto.EventRequest
import com.eventcreator.dto.EventResponse
import com.eventcreator.models.Events
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.eventcreator.routes.configureRouting
import com.eventcreator.services.NotFoundException
import com.eventcreator.services.ValidationException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import kotlinx.serialization.decodeFromString
import kotlin.test.*

class EventRoutesTest {

    private val json = Json { ignoreUnknownKeys = true }

    // Configura una instancia de la app con H2 en memoria
    private fun Application.testModule() {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
            driver = "org.h2.Driver"
        )
        transaction { SchemaUtils.create(Events) }

        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(StatusPages) {
            exception<ValidationException> { call, cause ->
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.errors.joinToString("; ")))
            }
            exception<NotFoundException> { call, cause ->
                call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "No encontrado"))
            }
        }
        configureRouting()
    }

    @Test
    fun `GET api-events returns empty list initially`() = testApplication {
        application { testModule() }
        val response = client.get("/api/events")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("[]"))
    }

    @Test
    fun `POST api-events creates event and returns 201`() = testApplication {
        application { testModule() }
        val client = createClient {
            install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val response = client.post("/api/events") {
            contentType(ContentType.Application.Json)
            setBody(EventRequest(
                title = "Test Event",
                description = "A test event",
                eventDate = "2026-06-01",
                eventTime = "10:00",
                location = "Test Location",
                category = "Test",
                capacity = 100,
                price = 9.99,
                organizer = "Test Organizer",
                status = "draft"
            ))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val event = json.decodeFromString<EventResponse>(response.bodyAsText())
        assertEquals("Test Event", event.title)
        assertEquals("draft", event.status)
    }

    @Test
    fun `POST api-events with missing title returns 400`() = testApplication {
        application { testModule() }
        val client = createClient {
            install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val response = client.post("/api/events") {
            contentType(ContentType.Application.Json)
            setBody(EventRequest(
                title = "",
                eventDate = "2026-06-01",
                eventTime = "10:00",
                location = "Location",
                category = "Cat",
                capacity = 10,
                price = 0.0,
                organizer = "Org",
                status = "draft"
            ))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET api-events-{id} returns 404 for unknown id`() = testApplication {
        application { testModule() }
        val response = client.get("/api/events/00000000-0000-0000-0000-000000000000")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE api-events-{id} removes event`() = testApplication {
        application { testModule() }
        val client = createClient {
            install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        // Crear primero
        val created = client.post("/api/events") {
            contentType(ContentType.Application.Json)
            setBody(EventRequest(
                title = "To Delete",
                eventDate = "2026-06-01",
                eventTime = "10:00",
                location = "Location",
                category = "Cat",
                capacity = 10,
                price = 0.0,
                organizer = "Org",
                status = "draft"
            ))
        }
        val event = json.decodeFromString<EventResponse>(created.bodyAsText())

        // Borrar
        val deleteResponse = client.delete("/api/events/${event.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // Verificar que ya no existe
        val getResponse = client.get("/api/events/${event.id}")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }
}
