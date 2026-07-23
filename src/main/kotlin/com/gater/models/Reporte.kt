package com.gater.models

import java.time.LocalDateTime

data class Reporte(
    val id: Int,
    val usuarioId: Int?,
    val tipoEmergencia: String,
    val descripcion: String,
    val nivelEmergencia: String,
    val latitud: Double,
    val longitud: Double,
    val direccionReferencia: String?,
    val estado: String,
    val fechaCreacion: LocalDateTime
)