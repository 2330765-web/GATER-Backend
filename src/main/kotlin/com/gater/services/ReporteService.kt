package com.gater.services

import com.gater.database.EstadoReporte
import com.gater.database.NivelEmergencia
import com.gater.dto.ActualizarReporteRequest
import com.gater.dto.CrearReporteRequest
import com.gater.dto.ReporteResponse
import com.gater.models.Reporte
import com.gater.repositories.ReporteRepository

object ReporteService {

    fun crear(
        request: CrearReporteRequest
    ): ReporteResponse {

        validarCreacion(request)

        val reporte =
            ReporteRepository.crear(request)

        return reporteAResponse(reporte)
    }

    fun listar(): List<ReporteResponse> {

        return ReporteRepository
            .listar()
            .map(::reporteAResponse)
    }

    fun obtenerPorId(
        id: Int
    ): ReporteResponse? {

        validarId(id)

        return ReporteRepository
            .obtenerPorId(id)
            ?.let(::reporteAResponse)
    }

    fun actualizar(
        id: Int,
        request: ActualizarReporteRequest
    ): ReporteResponse? {

        validarId(id)

        request.tipoEmergencia?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "El tipo de emergencia no puede estar vacío"
                )
            }
        }

        request.descripcion?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "La descripción no puede estar vacía"
                )
            }
        }

        request.nivelEmergencia?.let {
            validarNivelEmergencia(it)
        }

        request.estado?.let {
            validarEstadoReporte(it)
        }

        request.latitud?.let {
            validarLatitud(it)
        }

        request.longitud?.let {
            validarLongitud(it)
        }

        return ReporteRepository
            .actualizar(id, request)
            ?.let(::reporteAResponse)
    }

    fun eliminar(id: Int): Boolean {

        validarId(id)

        return ReporteRepository.eliminar(id)
    }

    private fun validarCreacion(
        request: CrearReporteRequest
    ) {

        if (request.tipoEmergencia.isBlank()) {
            throw IllegalArgumentException(
                "El tipo de emergencia es obligatorio"
            )
        }

        if (request.descripcion.isBlank()) {
            throw IllegalArgumentException(
                "La descripción es obligatoria"
            )
        }

        validarNivelEmergencia(
            request.nivelEmergencia
        )

        validarLatitud(request.latitud)
        validarLongitud(request.longitud)

        request.usuarioId?.let {
            if (it <= 0) {
                throw IllegalArgumentException(
                    "El ID del usuario no es válido"
                )
            }
        }
    }

    private fun validarNivelEmergencia(
        nivel: String
    ) {
        try {
            NivelEmergencia.valueOf(
                nivel.trim().uppercase()
            )
        } catch (error: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Nivel de emergencia no válido. " +
                        "Usa ROJO, AMARILLO o VERDE"
            )
        }
    }

    private fun validarEstadoReporte(
        estado: String
    ) {
        try {
            EstadoReporte.valueOf(
                estado.trim().uppercase()
            )
        } catch (error: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Estado no válido. Usa PENDIENTE, " +
                        "ASIGNADO, EN_ATENCION, ATENDIDO o CANCELADO"
            )
        }
    }

    private fun validarLatitud(
        latitud: Double
    ) {
        if (latitud < -90.0 || latitud > 90.0) {
            throw IllegalArgumentException(
                "La latitud debe estar entre -90 y 90"
            )
        }
    }

    private fun validarLongitud(
        longitud: Double
    ) {
        if (longitud < -180.0 || longitud > 180.0) {
            throw IllegalArgumentException(
                "La longitud debe estar entre -180 y 180"
            )
        }
    }

    private fun validarId(id: Int) {
        if (id <= 0) {
            throw IllegalArgumentException(
                "El ID del reporte no es válido"
            )
        }
    }

    private fun reporteAResponse(
        reporte: Reporte
    ): ReporteResponse {

        return ReporteResponse(
            id = reporte.id,
            usuarioId = reporte.usuarioId,
            tipoEmergencia =
                reporte.tipoEmergencia,
            descripcion =
                reporte.descripcion,
            nivelEmergencia =
                reporte.nivelEmergencia,
            latitud =
                reporte.latitud,
            longitud =
                reporte.longitud,
            direccionReferencia =
                reporte.direccionReferencia,
            estado =
                reporte.estado,
            fechaCreacion =
                reporte.fechaCreacion.toString()
        )
    }
}