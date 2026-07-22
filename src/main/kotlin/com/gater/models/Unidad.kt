package com.gater.models

data class Unidad(
    val id: Int,
    val numero: String,
    val tipo: String,
    val placas: String?,
    val estado: String,
    val operador: String?,
    val capacidad: Int?,
    val activo: Boolean
)