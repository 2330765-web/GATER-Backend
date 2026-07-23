package com.gater.routes

import com.gater.dto.ActualizarTrasladoRequest
import com.gater.dto.CrearTrasladoRequest
import com.gater.dto.MensajeResponse
import com.gater.services.TrasladoService
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

fun Route.trasladoRoutes() {

    route("/traslados") {

        // POST /traslados
        post {
            try {
                val request =
                    call.receive<CrearTrasladoRequest>()

                val traslado =
                    TrasladoService.crear(request)

                call.respond(
                    HttpStatusCode.Created,
                    traslado
                )

            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "Datos del traslado no válidos"
                    )
                )

            } catch (error: Exception) {
                println("ERROR AL CREAR TRASLADO")
                println(error.message)
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje =
                            "No fue posible crear el traslado"
                    )
                )
            }
        }

        // GET /traslados
        get {
            try {
                call.respond(
                    HttpStatusCode.OK,
                    TrasladoService.listar()
                )

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje =
                            "No fue posible consultar los traslados"
                    )
                )
            }
        }

        // GET /traslados/{id}
        get("/{id}") {

            val id =
                call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje =
                            "El ID del traslado no es válido"
                    )
                )
                return@get
            }

            try {
                val traslado =
                    TrasladoService.obtenerPorId(id)

                if (traslado == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje =
                                "Traslado no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        traslado
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
                        mensaje =
                            "No fue posible consultar el traslado"
                    )
                )
            }
        }

        // PUT /traslados/{id}
        put("/{id}") {

            val id =
                call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje =
                            "El ID del traslado no es válido"
                    )
                )
                return@put
            }

            try {
                val request =
                    call.receive<ActualizarTrasladoRequest>()

                val traslado =
                    TrasladoService.actualizar(
                        id,
                        request
                    )

                if (traslado == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje =
                                "Traslado no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        traslado
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
                        mensaje =
                            "No fue posible actualizar el traslado"
                    )
                )
            }
        }

        // DELETE /traslados/{id}
        delete("/{id}") {

            val id =
                call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje =
                            "El ID del traslado no es válido"
                    )
                )
                return@delete
            }

            try {
                val eliminado =
                    TrasladoService.eliminar(id)

                if (!eliminado) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje =
                                "Traslado no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MensajeResponse(
                            mensaje =
                                "Traslado eliminado correctamente"
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
                        mensaje =
                            "No fue posible eliminar el traslado"
                    )
                )
            }
        }
    }
}