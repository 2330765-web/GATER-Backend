package com.gater.services

import com.gater.dto.ActualizarHospitalRequest
import com.gater.dto.CrearHospitalRequest
import com.gater.dto.HospitalResponse
import com.gater.models.Hospital
import com.gater.repositories.HospitalRepository

object HospitalService {

    fun crear(request: CrearHospitalRequest): HospitalResponse {

        validarDatosCreacion(request)

        val hospital = HospitalRepository.crear(request)

        return hospitalAResponse(hospital)
    }

    fun listar(): List<HospitalResponse> {
        return HospitalRepository
            .listar()
            .map(::hospitalAResponse)
    }

    fun obtenerPorId(id: Int): HospitalResponse? {
        return HospitalRepository
            .obtenerPorId(id)
            ?.let(::hospitalAResponse)
    }

    fun actualizar(
        id: Int,
        request: ActualizarHospitalRequest
    ): HospitalResponse? {

        if (id <= 0) {
            throw IllegalArgumentException(
                "El ID del hospital no es válido"
            )
        }

        request.nombre?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "El nombre del hospital no puede estar vacío"
                )
            }
        }

        request.direccion?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "La dirección no puede estar vacía"
                )
            }
        }

        request.tipo?.let {
            if (it.isBlank()) {
                throw IllegalArgumentException(
                    "El tipo de hospital no puede estar vacío"
                )
            }
        }

        return HospitalRepository
            .actualizar(id, request)
            ?.let(::hospitalAResponse)
    }

    fun eliminar(id: Int): Boolean {

        if (id <= 0) {
            throw IllegalArgumentException(
                "El ID del hospital no es válido"
            )
        }

        return HospitalRepository.eliminar(id)
    }

    private fun validarDatosCreacion(
        request: CrearHospitalRequest
    ) {
        if (request.nombre.isBlank()) {
            throw IllegalArgumentException(
                "El nombre del hospital es obligatorio"
            )
        }

        if (request.direccion.isBlank()) {
            throw IllegalArgumentException(
                "La dirección es obligatoria"
            )
        }

        if (request.municipio.isBlank()) {
            throw IllegalArgumentException(
                "El municipio es obligatorio"
            )
        }

        if (request.estado.isBlank()) {
            throw IllegalArgumentException(
                "El estado es obligatorio"
            )
        }

        if (request.tipo.isBlank()) {
            throw IllegalArgumentException(
                "El tipo de hospital es obligatorio"
            )
        }
    }

    private fun hospitalAResponse(
        hospital: Hospital
    ): HospitalResponse {
        return HospitalResponse(
            id = hospital.id,
            nombre = hospital.nombre,
            direccion = hospital.direccion,
            municipio = hospital.municipio,
            estado = hospital.estado,
            especialidad = hospital.especialidad,
            telefono = hospital.telefono,
            tipo = hospital.tipo,
            activo = hospital.activo
        )
    }
}