package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearHospitalRequest(
    val nombre: String,
    val direccion: String,
    val municipio: String,
    val estado: String,
    val especialidad: String? = null,
    val telefono: String? = null,
    val tipo: String,
    val activo: Boolean = true
)

@Serializable
data class ActualizarHospitalRequest(
    val nombre: String? = null,
    val direccion: String? = null,
    val municipio: String? = null,
    val estado: String? = null,
    val especialidad: String? = null,
    val telefono: String? = null,
    val tipo: String? = null,
    val activo: Boolean? = null
)

@Serializable
data class HospitalResponse(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val municipio: String,
    val estado: String,
    val especialidad: String?,
    val telefono: String?,
    val tipo: String,
    val activo: Boolean
)