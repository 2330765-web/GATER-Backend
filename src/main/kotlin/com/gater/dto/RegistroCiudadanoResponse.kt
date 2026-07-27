package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistroCiudadanoResponse(
    val mensaje: String,
    val usuario: UsuarioResponse
)