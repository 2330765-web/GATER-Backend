package com.gater

import com.gater.config.configureSerialization
import io.ktor.server.application.Application

fun Application.rootModule() {
    configureSerialization()
    configureRouting()
}