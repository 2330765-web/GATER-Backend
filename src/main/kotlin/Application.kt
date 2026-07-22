package com.gater

import com.gater.config.configureSerialization
import com.gater.database.DatabaseFactory
import io.ktor.server.application.Application

fun Application.rootModule() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()
}