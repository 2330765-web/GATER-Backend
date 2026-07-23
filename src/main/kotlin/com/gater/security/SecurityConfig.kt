package com.gater.security

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity() {

    install(Authentication) {

        jwt("auth-jwt") {

            realm = JwtConfig.realm

            verifier(JwtConfig.verifier)

            validate { credential ->

                val usuarioId = credential.payload
                    .getClaim("usuarioId")
                    .asInt()

                if (usuarioId != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}