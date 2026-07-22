package com.gater.services

import com.gater.database.EstadoUnidad
import com.gater.dto.ActualizarUnidadRequest
import com.gater.dto.CrearUnidadRequest
import com.gater.dto.UnidadResponse
import com.gater.models.Unidad
import com.gater.repositories.UnidadRepository

object UnidadService {

    fun crear(request: CrearUnidadRequest): UnidadResponse {

        validarCreacion(request)

        val unidadExistente =
            UnidadRepository.obtenerPorNumero(request.numero)

        if (unidadExistente != null) {
            throw IllegalArgumentException(
                "Ya existe una unidad con ese número"
            )
        }

        val unidad = UnidadRepository.crear(request)

        return unidadAResponse(unidad)
    }

    fun listar(): List<UnidadResponse> {
        return UnidadRepository
            .listar()
            .map(::unidadAResponse)
    }

    fun obtenerPorId(id: Int): UnidadResponse? {

        validarId(id)

        return UnidadRepository
            .obtenerPorId(id)
            ?.let(::unidadAResponse)
    }

    fun actualizar(
        id: Int,
        request: ActualizarUnidadRequest
    ): UnidadResponse? {

        validarId(id)

        request.numero?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "El número de la unidad no puede estar vacío"
                )
            }

            val existente =
                UnidadRepository.obtenerPorNumero(it)

            if (existente != null && existente.id != id) {
                throw IllegalArgumentException(
                    "Ya existe otra unidad con ese número"
                )
            }
        }

        request.tipo?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "El tipo de unidad no puede estar vacío"
                )
            }
        }

        request.estado?.let {
            validarEstado(it)
        }

        request.capacidad?.let {
            if (it < 0) {
                throw IllegalArgumentException(
                    "La capacidad no puede ser negativa"
                )
            }
        }

        return UnidadRepository
            .actualizar(id, request)
            ?.let(::unidadAResponse)
    }

    fun eliminar(id: Int): Boolean {

        validarId(id)

        return UnidadRepository.eliminar(id)
    }

    private fun validarCreacion(
        request: CrearUnidadRequest
    ) {
        if (request.numero.isBlank()) {
            throw IllegalArgumentException(
                "El número de la unidad es obligatorio"
            )
        }

        if (request.tipo.isBlank()) {
            throw IllegalArgumentException(
                "El tipo de unidad es obligatorio"
            )
        }

        validarEstado(request.estado)

        request.capacidad?.let {
            if (it < 0) {
                throw IllegalArgumentException(
                    "La capacidad no puede ser negativa"
                )
            }
        }
    }

    private fun validarEstado(estado: String) {
        try {
            EstadoUnidad.valueOf(
                estado.trim().uppercase()
            )
        } catch (error: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Estado de unidad no válido. Usa: " +
                        "DISPONIBLE, OCUPADA, MANTENIMIENTO o FUERA_DE_SERVICIO"
            )
        }
    }

    private fun validarId(id: Int) {
        if (id <= 0) {
            throw IllegalArgumentException(
                "El ID de la unidad no es válido"
            )
        }
    }

    private fun unidadAResponse(
        unidad: Unidad
    ): UnidadResponse {
        return UnidadResponse(
            id = unidad.id,
            numero = unidad.numero,
            tipo = unidad.tipo,
            placas = unidad.placas,
            estado = unidad.estado,
            operador = unidad.operador,
            capacidad = unidad.capacidad,
            activo = unidad.activo
        )
    }
}