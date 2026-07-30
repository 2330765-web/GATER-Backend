package com.gater.services

object CorreoService {

    fun enviarCodigoVerificacion(
        destinatario: String,
        nombre: String,
        codigo: String
    ) {
        println(
            """
            ===== VERIFICACIÓN DE CORREO =====
            Para: $destinatario
            Nombre: $nombre
            Código: $codigo
            =================================
            """.trimIndent()
        )
    }
}
