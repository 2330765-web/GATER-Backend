package com.gater

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {

        // Endpoint principal del sistema
        get("/") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "sistema" to "GATER",
                    "version" to "1.0",
                    "estado" to "Activo",
                    "mensaje" to "Backend de GATER funcionando correctamente"
                )
            )
        }

        // Endpoint para comprobar el estado del servidor
        get("/health") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "status" to "OK"
                )
            )
        }
    }
}