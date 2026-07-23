package com.gater

import com.gater.routes.authRoutes
import com.gater.routes.hospitalRoutes
import com.gater.routes.reporteRoutes
import com.gater.routes.trasladoRoutes
import com.gater.routes.unidadRoutes
import com.gater.routes.usuarioRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {

        get("/") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "sistema" to "GATER",
                    "version" to "1.0",
                    "estado" to "Activo",
                    "mensaje" to
                            "Backend de GATER funcionando correctamente"
                )
            )
        }

        get("/health") {
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "status" to "OK"
                )
            )
        }

        usuarioRoutes()
        authRoutes()
        hospitalRoutes()
        unidadRoutes()
        reporteRoutes()
        trasladoRoutes()
    }
}