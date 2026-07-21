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
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)

    // Permite responder y recibir información en formato JSON
    implementation("io.ktor:ktor-server-content-negotiation:3.5.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.1")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}