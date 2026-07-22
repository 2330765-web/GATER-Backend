package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearUnidadRequest(
    val numero: String,
    val tipo: String,
    val placas: String? = null,
    val estado: String,
    val operador: String? = null,
    val capacidad: Int? = null,
    val activo: Boolean = true
)

@Serializable
data class ActualizarUnidadRequest(
    val numero: String? = null,
    val tipo: String? = null,
    val placas: String? = null,
    val estado: String? = null,
    val operador: String? = null,
    val capacidad: Int? = null,
    val activo: Boolean? = null
)

@Serializable
data class UnidadResponse(
    val id: Int,
    val numero: String,
    val tipo: String,
    val placas: String?,
    val estado: String,
    val operador: String?,
    val capacidad: Int?,
    val activo: Boolean
)