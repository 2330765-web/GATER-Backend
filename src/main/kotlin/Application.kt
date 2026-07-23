package com.gater

import com.gater.config.configureSerialization
import com.gater.database.DatabaseFactory
import com.gater.security.configureSecurity
import io.ktor.server.application.Application

fun Application.rootModule() {

    val conexionCorrecta = DatabaseFactory.init()

    if (!conexionCorrecta) {
        throw IllegalStateException(
            "El backend no pudo conectarse con MySQL"
        )
    }

    configureSerialization()
    configureSecurity()
    configureRouting()
}