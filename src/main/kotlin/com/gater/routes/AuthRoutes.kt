package com.gater.routes

import com.gater.dto.LoginRequest
import com.gater.dto.MensajeResponse
import com.gater.dto.RegistroCiudadanoRequest
import com.gater.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes() {

    post("/login") {
        try {
            val request = call.receive<LoginRequest>()
            val response = AuthService.login(request)

            call.respond(
                status = HttpStatusCode.OK,
                message = response
            )

        } catch (error: IllegalArgumentException) {

            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = MensajeResponse(
                    mensaje = error.message
                        ?: "Correo o contraseña incorrectos"
                )
            )

        } catch (error: Exception) {

            error.printStackTrace()

            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = MensajeResponse(
                    mensaje = "Ocurrió un error al iniciar sesión"
                )
            )
        }
    }

    post("/registro") {

        try {

            val request =
                call.receive<RegistroCiudadanoRequest>()

            val response =
                AuthService.registrarCiudadano(request)

            call.respond(
                status = HttpStatusCode.Created,
                message = response
            )

        } catch (error: IllegalArgumentException) {

            call.respond(
                status = HttpStatusCode.BadRequest,
                message = MensajeResponse(
                    mensaje = error.message ?: "Datos inválidos"
                )
            )

        } catch (error: Exception) {

            error.printStackTrace()

            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = MensajeResponse(
                    mensaje = "Error al registrar ciudadano"
                )
            )
        }
    }
}
