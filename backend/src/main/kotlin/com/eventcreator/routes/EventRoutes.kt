package com.eventcreator.routes

import com.eventcreator.dto.ErrorResponse
import com.eventcreator.dto.EventRequest
import com.eventcreator.services.EventService
import com.eventcreator.services.NotFoundException
import com.eventcreator.services.ValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(service: EventService = EventService()) {
    routing {
        route("/api/events") {

            // GET /api/events?search=&category=&status=&sort=
            get {
                val search   = call.request.queryParameters["search"]
                val category = call.request.queryParameters["category"]
                val status   = call.request.queryParameters["status"]
                val sort     = call.request.queryParameters["sort"]
                call.respond(service.listEvents(search, category, status, sort))
            }

            // GET /api/events/{id}
            get("{id}") {
                val id = call.parameters["id"]!!
                call.respond(service.getEvent(id))
            }

            // POST /api/events
            post {
                val req = call.receive<EventRequest>()
                val created = service.createEvent(req)
                call.respond(HttpStatusCode.Created, created)
            }

            // PUT /api/events/{id}
            put("{id}") {
                val id  = call.parameters["id"]!!
                val req = call.receive<EventRequest>()
                call.respond(service.updateEvent(id, req))
            }

            // DELETE /api/events/{id}
            delete("{id}") {
                val id = call.parameters["id"]!!
                service.deleteEvent(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
