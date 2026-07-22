package com.gater

import com.gater.routes.authRoutes
import com.gater.routes.usuarioRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {

        // Página principal
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

        // Health Check
        get("/health") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "status" to "OK"
                )
            )
        }

        // CRUD de usuarios
        usuarioRoutes()

        // Login
        authRoutes()
    }
}