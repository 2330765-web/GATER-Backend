package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearUsuarioRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: String,
    val area: String? = null,
    val telefono: String? = null
)

@Serializable
data class ActualizarUsuarioRequest(
    val nombre: String? = null,
    val rol: String? = null,
    val area: String? = null,
    val telefono: String? = null,
    val activo: Boolean? = null
)

@Serializable
data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String,
    val area: String?,
    val telefono: String?,
    val activo: Boolean
)

@Serializable
data class MensajeResponse(
    val mensaje: String
)