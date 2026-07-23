package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearReporteRequest(
    val usuarioId: Int? = null,
    val tipoEmergencia: String,
    val descripcion: String,
    val nivelEmergencia: String,
    val latitud: Double,
    val longitud: Double,
    val direccionReferencia: String? = null
)

@Serializable
data class ActualizarReporteRequest(
    val tipoEmergencia: String? = null,
    val descripcion: String? = null,
    val nivelEmergencia: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val direccionReferencia: String? = null,
    val estado: String? = null
)

@Serializable
data class ReporteResponse(
    val id: Int,
    val usuarioId: Int?,
    val tipoEmergencia: String,
    val descripcion: String,
    val nivelEmergencia: String,
    val latitud: Double,
    val longitud: Double,
    val direccionReferencia: String?,
    val estado: String,
    val fechaCreacion: String
)