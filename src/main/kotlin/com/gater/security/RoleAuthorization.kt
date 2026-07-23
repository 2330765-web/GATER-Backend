package com.gater.security

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond

suspend fun ApplicationCall.validarRol(
    vararg rolesPermitidos: String
): Boolean {

    val principal = principal<JWTPrincipal>()

    if (principal == null) {
        respond(
            HttpStatusCode.Unauthorized,
            mapOf(
                "mensaje" to "Usuario no autenticado"
            )
        )
        return false
    }

    val rol = principal.payload
        .getClaim("rol")
        .asString()
        ?.uppercase()

    if (
        rol == null ||
        rol !in rolesPermitidos.map { it.uppercase() }
    ) {
        respond(
            HttpStatusCode.Forbidden,
            mapOf(
                "mensaje" to
                        "No tienes permisos para realizar esta acción"
            )
        )
        return false
    }

    return true
}