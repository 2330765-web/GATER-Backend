package com.gater.routes

import com.gater.dto.ActualizarUsuarioRequest
import com.gater.dto.CrearUsuarioRequest
import com.gater.dto.MensajeResponse
import com.gater.services.UsuarioService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.usuarioRoutes() {

    route("/usuarios") {

        post {
            try {
                val request = call.receive<CrearUsuarioRequest>()
                val usuario = UsuarioService.crear(request)

                call.respond(
                    status = HttpStatusCode.Created,
                    message = usuario
                )
            } catch (error: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = error.message ?: "Datos inválidos"
                    )
                )
            } catch (error: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = "No fue posible crear el usuario"
                    )
                )
            }
        }

        get {
            try {
                val usuarios = UsuarioService.listar()
                call.respond(usuarios)
            } catch (error: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = "No fue posible consultar los usuarios"
                    )
                )
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = "El ID del usuario no es válido"
                    )
                )
                return@get
            }

            val usuario = UsuarioService.obtenerPorId(id)

            if (usuario == null) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = MensajeResponse(
                        mensaje = "Usuario no encontrado"
                    )
                )
            } else {
                call.respond(usuario)
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = "El ID del usuario no es válido"
                    )
                )
                return@put
            }

            try {
                val request = call.receive<ActualizarUsuarioRequest>()
                val usuario = UsuarioService.actualizar(id, request)

                if (usuario == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = MensajeResponse(
                            mensaje = "Usuario no encontrado"
                        )
                    )
                } else {
                    call.respond(usuario)
                }
            } catch (error: IllegalArgumentException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = error.message ?: "Datos inválidos"
                    )
                )
            } catch (error: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = "No fue posible actualizar el usuario"
                    )
                )
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = "El ID del usuario no es válido"
                    )
                )
                return@delete
            }

            val eliminado = UsuarioService.eliminar(id)

            if (!eliminado) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = MensajeResponse(
                        mensaje = "Usuario no encontrado"
                    )
                )
            } else {
                call.respond(
                    MensajeResponse(
                        mensaje = "Usuario eliminado correctamente"
                    )
                )
            }
        }
    }
}