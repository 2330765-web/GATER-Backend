package com.gater.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {

    private val secret: String =
        System.getenv("JWT_SECRET")
            ?: "gater-clave-local-temporal-cambiar-en-railway"

    private val issuer: String =
        System.getenv("JWT_ISSUER")
            ?: "gater-backend"

    private val audience: String =
        System.getenv("JWT_AUDIENCE")
            ?: "gater-mobile"

    val realm: String =
        System.getenv("JWT_REALM")
            ?: "GATER"

    private val algorithm =
        Algorithm.HMAC256(secret)

    val verifier =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

    fun generarToken(
        usuarioId: Int,
        correo: String,
        rol: String
    ): String {

        val ahora = System.currentTimeMillis()

        val expiracion = Date(
            ahora + 24L * 60L * 60L * 1000L
        )

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("usuarioId", usuarioId)
            .withClaim("correo", correo)
            .withClaim("rol", rol)
            .withIssuedAt(Date(ahora))
            .withExpiresAt(expiracion)
            .sign(algorithm)
    }
}