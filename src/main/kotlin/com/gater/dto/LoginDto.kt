package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val correo: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val mensaje: String,
    val usuario: UsuarioResponse
)