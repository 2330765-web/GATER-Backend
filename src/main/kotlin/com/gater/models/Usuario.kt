package com.gater.models

data class Usuario(

    val id: Int,

    val nombre: String,

    val correo: String,

    val passwordHash: String,

    val rol: String,

    val area: String?,

    val telefono: String?,

    val activo: Boolean

)