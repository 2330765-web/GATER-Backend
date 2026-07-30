package com.gater.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object CorreoService {

    private const val RESEND_URL =
        "https://api.resend.com/emails"

    /*
     * Para las primeras pruebas con Resend.
     *
     * Sin un dominio propio verificado, Resend normalmente
     * limita los destinatarios permitidos. Más adelante
     * cambiaremos este remitente por uno de tu dominio.
     */
    private const val REMITENTE =
        "GATER <onboarding@resend.dev>"

    private val cliente = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }

        expectSuccess = false
    }

    fun enviarCodigoVerificacion(
        destinatario: String,
        nombre: String,
        codigo: String
    ) {
        runBlocking {
            enviarCorreo(
                destinatario = destinatario,
                nombre = nombre,
                codigo = codigo
            )
        }
    }

    private suspend fun enviarCorreo(
        destinatario: String,
        nombre: String,
        codigo: String
    ) {
        val apiKey =
            System.getenv("RESEND_API_KEY")
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException(
                    "No está configurada la variable RESEND_API_KEY"
                )

        val solicitud = ResendEmailRequest(
            from = REMITENTE,
            to = listOf(destinatario),
            subject = "Código de verificación de GATER",
            html = crearContenidoHtml(
                nombre = nombre,
                codigo = codigo
            )
        )

        val respuesta: HttpResponse =
            cliente.post(RESEND_URL) {
                bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(solicitud)
            }

        if (respuesta.status != HttpStatusCode.OK) {
            val detalle =
                runCatching {
                    respuesta.body<String>()
                }.getOrDefault(
                    "Resend no devolvió información adicional"
                )

            throw IllegalStateException(
                "No fue posible enviar el correo. " +
                        "Estado: ${respuesta.status.value}. " +
                        "Detalle: $detalle"
            )
        }

        val resultado =
            runCatching {
                respuesta.body<ResendEmailResponse>()
            }.getOrNull()

        println(
            "Correo de verificación enviado a $destinatario. " +
                    "ID: ${resultado?.id ?: "sin ID"}"
        )
    }

    private fun crearContenidoHtml(
        nombre: String,
        codigo: String
    ): String {
        val nombreSeguro =
            escaparHtml(nombre)

        val codigoSeguro =
            escaparHtml(codigo)

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">
            </head>

            <body style="
                margin: 0;
                padding: 24px;
                background-color: #f4f6f8;
                font-family: Arial, Helvetica, sans-serif;
            ">
                <div style="
                    max-width: 520px;
                    margin: 0 auto;
                    padding: 32px;
                    background-color: #ffffff;
                    border-radius: 12px;
                ">
                    <h1 style="
                        margin-top: 0;
                        text-align: center;
                        color: #1f2937;
                    ">
                        Verifica tu correo
                    </h1>

                    <p style="
                        color: #374151;
                        font-size: 16px;
                    ">
                        Hola, $nombreSeguro:
                    </p>

                    <p style="
                        color: #374151;
                        font-size: 16px;
                    ">
                        Usa este código para verificar tu cuenta
                        de GATER:
                    </p>

                    <div style="
                        margin: 28px 0;
                        padding: 18px;
                        text-align: center;
                        background-color: #eef2ff;
                        border-radius: 10px;
                        font-size: 32px;
                        font-weight: bold;
                        letter-spacing: 8px;
                        color: #3730a3;
                    ">
                        $codigoSeguro
                    </div>

                    <p style="
                        color: #4b5563;
                        font-size: 14px;
                    ">
                        Este código vence en 15 minutos.
                    </p>

                    <p style="
                        color: #4b5563;
                        font-size: 14px;
                    ">
                        Si no solicitaste esta cuenta, puedes
                        ignorar este mensaje.
                    </p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun escaparHtml(
        texto: String
    ): String {
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}

@Serializable
private data class ResendEmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)

@Serializable
private data class ResendEmailResponse(
    @SerialName("id")
    val id: String
)
