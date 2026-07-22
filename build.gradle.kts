plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

group = "com.gater"
version = "1.0.0-SNAPSHOT"

application {
    mainClass.set("com.gater.MainKt")
}

kotlin {
    jvmToolchain(21)
}

ktor {
    fatJar {
        archiveFileName.set("gater-backend-all.jar")
    }
}

dependencies {
    // Servidor Ktor
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)

    // JSON
    implementation("io.ktor:ktor-server-content-negotiation:3.5.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.0")

    // MySQL: controlador oficial JDBC
    implementation("com.mysql:mysql-connector-j:9.7.0")

    // Grupo de conexiones a la base de datos
    implementation("com.zaxxer:HikariCP:7.0.2")

    // Exposed: acceso a MySQL desde Kotlin
    implementation("org.jetbrains.exposed:exposed-core:1.3.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.3.1")
    implementation("org.jetbrains.exposed:exposed-java-time:1.3.1")

    // Seguridad para contraseñas
    implementation("org.mindrot:jbcrypt:0.4")

    // Pruebas
    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}