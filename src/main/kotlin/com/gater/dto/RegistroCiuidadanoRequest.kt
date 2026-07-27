package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistroCiudadanoRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val telefono: String? = null,
    val municipio: String? = null
)