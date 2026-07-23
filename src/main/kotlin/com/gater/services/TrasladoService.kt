package com.gater.services

import com.gater.database.EstadoTraslado
import com.gater.dto.ActualizarTrasladoRequest
import com.gater.dto.CrearTrasladoRequest
import com.gater.dto.TrasladoResponse
import com.gater.models.Traslado
import com.gater.repositories.HospitalRepository
import com.gater.repositories.TrasladoRepository
import com.gater.repositories.UnidadRepository
import com.gater.repositories.UsuarioRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

object TrasladoService {

    fun crear(
        request: CrearTrasladoRequest
    ): TrasladoResponse {

        validarCreacion(request)
        validarRelaciones(
            hospitalId = request.hospitalId,
            unidadId = request.unidadId,
            bomberoId = request.bomberoId
        )

        val traslado =
            TrasladoRepository.crear(request)

        return trasladoAResponse(traslado)
    }

    fun listar(): List<TrasladoResponse> {

        return TrasladoRepository
            .listar()
            .map(::trasladoAResponse)
    }

    fun obtenerPorId(
        id: Int
    ): TrasladoResponse? {

        validarId(id)

        return TrasladoRepository
            .obtenerPorId(id)
            ?.let(::trasladoAResponse)
    }

    fun actualizar(
        id: Int,
        request: ActualizarTrasladoRequest
    ): TrasladoResponse? {

        validarId(id)
        validarActualizacion(request)

        request.hospitalId?.let {
            if (HospitalRepository.obtenerPorId(it) == null) {
                throw IllegalArgumentException(
                    "El hospital indicado no existe"
                )
            }
        }

        request.unidadId?.let {
            if (UnidadRepository.obtenerPorId(it) == null) {
                throw IllegalArgumentException(
                    "La unidad indicada no existe"
                )
            }
        }

        request.bomberoId?.let {
            val usuario =
                UsuarioRepository.obtenerPorId(it)
                    ?: throw IllegalArgumentException(
                        "El usuario asignado no existe"
                    )

            if (
                usuario.rol != "BOMBERO" &&
                usuario.rol != "ADMINISTRADOR"
            ) {
                throw IllegalArgumentException(
                    "El usuario asignado debe ser BOMBERO o ADMINISTRADOR"
                )
            }
        }

        return TrasladoRepository
            .actualizar(id, request)
            ?.let(::trasladoAResponse)
    }

    fun eliminar(
        id: Int
    ): Boolean {

        validarId(id)

        return TrasladoRepository.eliminar(id)
    }

    private fun validarCreacion(
        request: CrearTrasladoRequest
    ) {
        if (request.paciente.isBlank()) {
            throw IllegalArgumentException(
                "El nombre del paciente es obligatorio"
            )
        }

        if (request.edad < 0 || request.edad > 120) {
            throw IllegalArgumentException(
                "La edad del paciente no es válida"
            )
        }

        if (request.hospitalId <= 0) {
            throw IllegalArgumentException(
                "El ID del hospital no es válido"
            )
        }

        validarFecha(request.fecha)
        validarHora(request.hora)
        validarEstado(request.estado)
    }

    private fun validarActualizacion(
        request: ActualizarTrasladoRequest
    ) {
        request.paciente?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "El nombre del paciente no puede quedar vacío"
                )
            }
        }

        request.edad?.let {
            if (it < 0 || it > 120) {
                throw IllegalArgumentException(
                    "La edad del paciente no es válida"
                )
            }
        }

        request.fecha?.let(::validarFecha)
        request.hora?.let(::validarHora)
        request.estado?.let(::validarEstado)
    }

    private fun validarRelaciones(
        hospitalId: Int,
        unidadId: Int?,
        bomberoId: Int?
    ) {
        if (
            HospitalRepository.obtenerPorId(hospitalId) == null
        ) {
            throw IllegalArgumentException(
                "El hospital indicado no existe"
            )
        }

        unidadId?.let {
            if (UnidadRepository.obtenerPorId(it) == null) {
                throw IllegalArgumentException(
                    "La unidad indicada no existe"
                )
            }
        }

        bomberoId?.let {
            val usuario =
                UsuarioRepository.obtenerPorId(it)
                    ?: throw IllegalArgumentException(
                        "El usuario asignado no existe"
                    )

            if (
                usuario.rol != "BOMBERO" &&
                usuario.rol != "ADMINISTRADOR"
            ) {
                throw IllegalArgumentException(
                    "El usuario asignado debe ser BOMBERO o ADMINISTRADOR"
                )
            }
        }
    }

    private fun validarFecha(
        fecha: String
    ) {
        try {
            LocalDate.parse(fecha)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "La fecha debe tener el formato AAAA-MM-DD"
            )
        }
    }

    private fun validarHora(
        hora: String
    ) {
        try {
            LocalTime.parse(hora)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "La hora debe tener el formato HH:mm"
            )
        }
    }

    private fun validarEstado(
        estado: String
    ) {
        try {
            EstadoTraslado.valueOf(
                estado.trim().uppercase()
            )
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Estado no válido. Usa PROGRAMADO, CONFIRMADO, " +
                        "EN_CURSO, FINALIZADO o CANCELADO"
            )
        }
    }

    private fun validarId(
        id: Int
    ) {
        if (id <= 0) {
            throw IllegalArgumentException(
                "El ID del traslado no es válido"
            )
        }
    }

    private fun trasladoAResponse(
        traslado: Traslado
    ): TrasladoResponse {

        return TrasladoResponse(
            id = traslado.id,
            paciente = traslado.paciente,
            edad = traslado.edad,
            telefono = traslado.telefono,
            hospitalId = traslado.hospitalId,
            unidadId = traslado.unidadId,
            bomberoId = traslado.bomberoId,
            fecha = traslado.fecha.toString(),
            hora = traslado.hora.toString(),
            requiereOxigeno =
                traslado.requiereOxigeno,
            requiereParamedico =
                traslado.requiereParamedico,
            estado = traslado.estado,
            observaciones = traslado.observaciones
        )
    }
}