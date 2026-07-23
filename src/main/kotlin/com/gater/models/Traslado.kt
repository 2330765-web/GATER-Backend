package com.gater.models

import java.time.LocalDate
import java.time.LocalTime

data class Traslado(
    val id: Int,
    val paciente: String,
    val edad: Int,
    val telefono: String?,
    val hospitalId: Int,
    val unidadId: Int?,
    val bomberoId: Int?,
    val fecha: LocalDate,
    val hora: LocalTime,
    val requiereOxigeno: Boolean,
    val requiereParamedico: Boolean,
    val estado: String,
    val observaciones: String?
)