package com.gater.routes

import com.gater.dto.ActualizarReporteRequest
import com.gater.dto.CrearReporteRequest
import com.gater.dto.MensajeResponse
import com.gater.services.ReporteService
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

fun Route.reporteRoutes() {

    route("/reportes") {

        // POST /reportes
        post {
            try {
                val request =
                    call.receive<CrearReporteRequest>()

                val reporte =
                    ReporteService.crear(request)

                call.respond(
                    HttpStatusCode.Created,
                    reporte
                )

            } catch (error: IllegalArgumentException) {

                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "Datos del reporte no válidos"
                    )
                )

            } catch (error: Exception) {

                println("ERROR AL CREAR REPORTE")
                println(error.message)
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje =
                            "No fue posible crear el reporte"
                    )
                )
            }
        }

        // GET /reportes
        get {
            try {
                call.respond(
                    HttpStatusCode.OK,
                    ReporteService.listar()
                )

            } catch (error: Exception) {

                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje =
                            "No fue posible consultar los reportes"
                    )
                )
            }
        }

        // GET /reportes/{id}
        get("/{id}") {

            val id =
                call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje =
                            "El ID del reporte no es válido"
                    )
                )
                return@get
            }

            try {
                val reporte =
                    ReporteService.obtenerPorId(id)

                if (reporte == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje =
                                "Reporte no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        reporte
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
                            "No fue posible consultar el reporte"
                    )
                )
            }
        }

        // PUT /reportes/{id}
        put("/{id}") {

            val id =
                call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje =
                            "El ID del reporte no es válido"
                    )
                )
                return@put
            }

            try {
                val request =
                    call.receive<ActualizarReporteRequest>()

                val reporte =
                    ReporteService.actualizar(
                        id,
                        request
                    )

                if (reporte == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje =
                                "Reporte no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        reporte
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
                            "No fue posible actualizar el reporte"
                    )
                )
            }
        }

        // DELETE /reportes/{id}
        delete("/{id}") {

            val id =
                call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje =
                            "El ID del reporte no es válido"
                    )
                )
                return@delete
            }

            try {
                val eliminado =
                    ReporteService.eliminar(id)

                if (!eliminado) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje =
                                "Reporte no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MensajeResponse(
                            mensaje =
                                "Reporte eliminado correctamente"
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
                            "No fue posible eliminar el reporte"
                    )
                )
            }
        }
    }
}