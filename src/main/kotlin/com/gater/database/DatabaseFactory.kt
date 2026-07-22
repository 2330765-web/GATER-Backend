package com.gater.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {

    private var inicializada = false

    fun init(): Boolean {

        if (inicializada) {
            return true
        }

        val host = System.getenv("MYSQLHOST")
        val port = System.getenv("MYSQLPORT")
        val databaseName = System.getenv("MYSQLDATABASE")
        val user = System.getenv("MYSQLUSER")
        val password = System.getenv("MYSQLPASSWORD")

        if (
            host.isNullOrBlank() ||
            port.isNullOrBlank() ||
            databaseName.isNullOrBlank() ||
            user.isNullOrBlank() ||
            password.isNullOrBlank()
        ) {
            println("MySQL no se inicializó: faltan variables de entorno.")
            return false
        }

        val hikariConfig = HikariConfig().apply {

            jdbcUrl =
                "jdbc:mysql://$host:$port/$databaseName" +
                        "?useSSL=false" +
                        "&allowPublicKeyRetrieval=true" +
                        "&serverTimezone=UTC"

            driverClassName = "com.mysql.cj.jdbc.Driver"

            username = user
            this.password = password

            maximumPoolSize = 5
            minimumIdle = 1

            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            connectionTimeout = 30_000
            validationTimeout = 5_000
            idleTimeout = 600_000
            maxLifetime = 1_800_000
        }

        return try {

            val dataSource = HikariDataSource(hikariConfig)

            Database.connect(dataSource)

            transaction {
                SchemaUtils.create(
                    UsuariosTable,
                    HospitalesTable,
                    UnidadesTable,
                    ReportesTable,
                    TrasladosTable
                )
            }

            inicializada = true

            println("==========================================")
            println("Conexión con MySQL realizada correctamente")
            println("Tablas de GATER verificadas correctamente")
            println("==========================================")

            true

        } catch (error: Exception) {

            println("==========================================")
            println("No fue posible conectar con MySQL")
            println("Tipo de error: ${error::class.simpleName}")
            println("Mensaje: ${error.message}")
            println("==========================================")

            false
        }
    }
}