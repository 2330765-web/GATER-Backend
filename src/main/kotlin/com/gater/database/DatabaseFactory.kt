package com.gater.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {

    private var inicializada = false

    lateinit var database: Database
        private set

    fun init(): Boolean {

        if (inicializada) {
            return true
        }

        val host = System.getenv("MYSQLHOST")
        val port = System.getenv("MYSQLPORT")
        val databaseName = System.getenv("MYSQLDATABASE")
        val user = System.getenv("MYSQLUSER")
        val password = System.getenv("MYSQLPASSWORD")

        println("==========================================")
        println("CONFIGURACIÓN MYSQL")
        println("HOST disponible: ${!host.isNullOrBlank()}")
        println("PORT disponible: ${!port.isNullOrBlank()}")
        println("DATABASE disponible: ${!databaseName.isNullOrBlank()}")
        println("USER disponible: ${!user.isNullOrBlank()}")
        println("PASSWORD disponible: ${!password.isNullOrBlank()}")
        println("==========================================")

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

        return try {

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

            val dataSource = HikariDataSource(hikariConfig)

            database = Database.connect(dataSource)

            inicializada = true

            println("==========================================")
            println("Conexión con MySQL realizada correctamente")
            println("Base de datos Exposed inicializada")
            println("==========================================")

            true

        } catch (error: Exception) {

            println("==========================================")
            println("No fue posible conectar con MySQL")
            println("Tipo: ${error::class.qualifiedName}")
            println("Mensaje: ${error.message}")
            error.printStackTrace()
            println("==========================================")

            false
        }
    }
}