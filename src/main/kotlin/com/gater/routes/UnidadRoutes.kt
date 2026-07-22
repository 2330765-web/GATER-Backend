package com.gater.routes

import com.gater.dto.ActualizarUnidadRequest
import com.gater.dto.CrearUnidadRequest
import com.gater.dto.MensajeResponse
import com.gater.services.UnidadService
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

fun Route.unidadRoutes() {

    route("/unidades") {

        post {
            try {
                val request =
                    call.receive<CrearUnidadRequest>()

                val unidad =
                    UnidadService.crear(request)

                call.respond(
                    HttpStatusCode.Created,
                    unidad
                )

            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "Datos de la unidad no válidos"
                    )
                )

            } catch (error: Exception) {
                println("ERROR AL CREAR UNIDAD")
                println(error.message)
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible crear la unidad"
                    )
                )
            }
        }

        get {
            try {
                call.respond(
                    HttpStatusCode.OK,
                    UnidadService.listar()
                )

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible consultar las unidades"
                    )
                )
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = "El ID de la unidad no es válido"
                    )
                )
                return@get
            }

            try {
                val unidad =
                    UnidadService.obtenerPorId(id)

                if (unidad == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje = "Unidad no encontrada"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        unidad
                    )
                }

            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "El ID no es válido"
                    )
                )

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible consultar la unidad"
                    )
                )
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = "El ID de la unidad no es válido"
                    )
                )
                return@put
            }

            try {
                val request =
                    call.receive<ActualizarUnidadRequest>()

                val unidad =
                    UnidadService.actualizar(id, request)

                if (unidad == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje = "Unidad no encontrada"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        unidad
                    )
                }

            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "Datos no válidos"
                    )
                )

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible actualizar la unidad"
                    )
                )
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = "El ID de la unidad no es válido"
                    )
                )
                return@delete
            }

            try {
                val eliminada =
                    UnidadService.eliminar(id)

                if (!eliminada) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje = "Unidad no encontrada"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MensajeResponse(
                            mensaje = "Unidad eliminada correctamente"
                        )
                    )
                }

            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "El ID no es válido"
                    )
                )

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible eliminar la unidad"
                    )
                )
            }
        }
    }
}