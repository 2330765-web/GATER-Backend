package com.gater.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearTrasladoRequest(
    val paciente: String,
    val edad: Int,
    val telefono: String? = null,
    val hospitalId: Int,
    val unidadId: Int? = null,
    val bomberoId: Int? = null,
    val fecha: String,
    val hora: String,
    val requiereOxigeno: Boolean = false,
    val requiereParamedico: Boolean = false,
    val estado: String = "PROGRAMADO",
    val observaciones: String? = null
)

@Serializable
data class ActualizarTrasladoRequest(
    val paciente: String? = null,
    val edad: Int? = null,
    val telefono: String? = null,
    val hospitalId: Int? = null,
    val unidadId: Int? = null,
    val bomberoId: Int? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val requiereOxigeno: Boolean? = null,
    val requiereParamedico: Boolean? = null,
    val estado: String? = null,
    val observaciones: String? = null
)

@Serializable
data class TrasladoResponse(
    val id: Int,
    val paciente: String,
    val edad: Int,
    val telefono: String?,
    val hospitalId: Int,
    val unidadId: Int?,
    val bomberoId: Int?,
    val fecha: String,
    val hora: String,
    val requiereOxigeno: Boolean,
    val requiereParamedico: Boolean,
    val estado: String,
    val observaciones: String?
)