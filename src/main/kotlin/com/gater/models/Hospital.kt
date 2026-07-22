package com.gater.models

data class Hospital(
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