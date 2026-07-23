package com.gater.routes

import com.gater.dto.ActualizarHospitalRequest
import com.gater.dto.CrearHospitalRequest
import com.gater.dto.MensajeResponse
import com.gater.security.validarRol
import com.gater.services.HospitalService
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

fun Route.hospitalRoutes() {

    route("/hospitales") {

        // POST /hospitales
        // Solo Administrador y Coordinador
        post {
            if (!call.validarRol("ADMINISTRADOR", "COORDINADOR")) {
                return@post
            }

            try {
                val request =
                    call.receive<CrearHospitalRequest>()

                val hospital =
                    HospitalService.crear(request)

                call.respond(
                    HttpStatusCode.Created,
                    hospital
                )

            } catch (error: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = error.message
                            ?: "Datos del hospital no válidos"
                    )
                )

            } catch (error: Exception) {
                println("ERROR AL CREAR HOSPITAL")
                println(error.message)
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible crear el hospital"
                    )
                )
            }
        }

        // GET /hospitales
        // Todos los usuarios autenticados
        get {
            if (
                !call.validarRol(
                    "ADMINISTRADOR",
                    "COORDINADOR",
                    "BOMBERO",
                    "CIUDADANO"
                )
            ) {
                return@get
            }

            try {
                call.respond(
                    HttpStatusCode.OK,
                    HospitalService.listar()
                )

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible consultar los hospitales"
                    )
                )
            }
        }

        // GET /hospitales/{id}
        // Todos los usuarios autenticados
        get("/{id}") {
            if (
                !call.validarRol(
                    "ADMINISTRADOR",
                    "COORDINADOR",
                    "BOMBERO",
                    "CIUDADANO"
                )
            ) {
                return@get
            }

            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = "El ID del hospital no es válido"
                    )
                )
                return@get
            }

            try {
                val hospital =
                    HospitalService.obtenerPorId(id)

                if (hospital == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje = "Hospital no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        hospital
                    )
                }

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible consultar el hospital"
                    )
                )
            }
        }

        // PUT /hospitales/{id}
        // Solo Administrador y Coordinador
        put("/{id}") {
            if (!call.validarRol("ADMINISTRADOR", "COORDINADOR")) {
                return@put
            }

            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = "El ID del hospital no es válido"
                    )
                )
                return@put
            }

            try {
                val request =
                    call.receive<ActualizarHospitalRequest>()

                val hospital =
                    HospitalService.actualizar(id, request)

                if (hospital == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje = "Hospital no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        hospital
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
                        mensaje = "No fue posible actualizar el hospital"
                    )
                )
            }
        }

        // DELETE /hospitales/{id}
        // Solo Administrador
        delete("/{id}") {
            if (!call.validarRol("ADMINISTRADOR")) {
                return@delete
            }

            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MensajeResponse(
                        mensaje = "El ID del hospital no es válido"
                    )
                )
                return@delete
            }

            try {
                val eliminado =
                    HospitalService.eliminar(id)

                if (!eliminado) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MensajeResponse(
                            mensaje = "Hospital no encontrado"
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MensajeResponse(
                            mensaje = "Hospital eliminado correctamente"
                        )
                    )
                }

            } catch (error: Exception) {
                error.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MensajeResponse(
                        mensaje = "No fue posible eliminar el hospital"
                    )
                )
            }
        }
    }
}