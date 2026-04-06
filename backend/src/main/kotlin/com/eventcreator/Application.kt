package com.eventcreator

import com.eventcreator.config.initDatabase
import com.eventcreator.dto.ErrorResponse
import com.eventcreator.routes.configureRouting
import com.eventcreator.services.NotFoundException
import com.eventcreator.services.ValidationException
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // CORS: allow any origin in development; restrict in production
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }

    // Centralised error handling
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.errors.joinToString("; ")))
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unexpected error", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error"))
        }
    }

    // Database
    val dbUrl      = environment.config.property("database.url").getString()
    val dbUser     = environment.config.property("database.user").getString()
    val dbPassword = environment.config.property("database.password").getString()
    initDatabase(dbUrl, dbUser, dbPassword)

    // Routes
    configureRouting()
}
