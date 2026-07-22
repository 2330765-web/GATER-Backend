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

        // Crear un usuario
        post {
            try {
                val request = call.receive<CrearUsuarioRequest>()

                val usuarioCreado = UsuarioService.crear(request)

                call.respond(
                    status = HttpStatusCode.Created,
                    message = usuarioCreado
                )

            } catch (error: IllegalArgumentException) {

                println("==========================================")
                println("ERROR DE VALIDACIÓN AL CREAR USUARIO")
                println("Mensaje: ${error.message}")
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = error.message ?: "Los datos enviados no son válidos"
                    )
                )

            } catch (error: Exception) {

                println("==========================================")
                println("ERROR INTERNO AL CREAR USUARIO")
                println("Tipo: ${error::class.qualifiedName}")
                println("Mensaje: ${error.message}")
                error.printStackTrace()
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = error.message
                            ?: "No fue posible crear el usuario"
                    )
                )
            }
        }

        // Consultar todos los usuarios
        get {
            try {
                val usuarios = UsuarioService.listar()

                call.respond(
                    status = HttpStatusCode.OK,
                    message = usuarios
                )

            } catch (error: Exception) {

                println("==========================================")
                println("ERROR AL CONSULTAR USUARIOS")
                println("Tipo: ${error::class.qualifiedName}")
                println("Mensaje: ${error.message}")
                error.printStackTrace()
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = error.message
                            ?: "No fue posible consultar los usuarios"
                    )
                )
            }
        }

        // Consultar un usuario por ID
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

            try {
                val usuario = UsuarioService.obtenerPorId(id)

                if (usuario == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = MensajeResponse(
                            mensaje = "Usuario no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = usuario
                    )
                }

            } catch (error: Exception) {

                println("==========================================")
                println("ERROR AL CONSULTAR USUARIO POR ID")
                println("ID: $id")
                println("Tipo: ${error::class.qualifiedName}")
                println("Mensaje: ${error.message}")
                error.printStackTrace()
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = error.message
                            ?: "No fue posible consultar el usuario"
                    )
                )
            }
        }

        // Actualizar un usuario
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

                val usuarioActualizado =
                    UsuarioService.actualizar(id, request)

                if (usuarioActualizado == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = MensajeResponse(
                            mensaje = "Usuario no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = usuarioActualizado
                    )
                }

            } catch (error: IllegalArgumentException) {

                println("==========================================")
                println("ERROR DE VALIDACIÓN AL ACTUALIZAR USUARIO")
                println("ID: $id")
                println("Mensaje: ${error.message}")
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = MensajeResponse(
                        mensaje = error.message ?: "Los datos enviados no son válidos"
                    )
                )

            } catch (error: Exception) {

                println("==========================================")
                println("ERROR INTERNO AL ACTUALIZAR USUARIO")
                println("ID: $id")
                println("Tipo: ${error::class.qualifiedName}")
                println("Mensaje: ${error.message}")
                error.printStackTrace()
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = error.message
                            ?: "No fue posible actualizar el usuario"
                    )
                )
            }
        }

        // Eliminar un usuario
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

            try {
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
                        status = HttpStatusCode.OK,
                        message = MensajeResponse(
                            mensaje = "Usuario eliminado correctamente"
                        )
                    )
                }

            } catch (error: Exception) {

                println("==========================================")
                println("ERROR AL ELIMINAR USUARIO")
                println("ID: $id")
                println("Tipo: ${error::class.qualifiedName}")
                println("Mensaje: ${error.message}")
                error.printStackTrace()
                println("==========================================")

                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = MensajeResponse(
                        mensaje = error.message
                            ?: "No fue posible eliminar el usuario"
                    )
                )
            }
        }
    }
}