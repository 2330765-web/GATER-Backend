package com.gater.services

import com.gater.dto.LoginRequest
import com.gater.dto.LoginResponse
import com.gater.dto.RegistroCiudadanoRequest
import com.gater.dto.RegistroCiudadanoResponse
import com.gater.dto.UsuarioResponse
import com.gater.models.Usuario
import com.gater.repositories.UsuarioRepository
import com.gater.security.JwtConfig
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import java.time.LocalDateTime

object AuthService {

    private const val DURACION_CODIGO_MINUTOS = 15L

    private val secureRandom = SecureRandom()

    fun login(
        request: LoginRequest
    ): LoginResponse {

        val correoNormalizado =
            request.correo
                .trim()
                .lowercase()

        if (correoNormalizado.isBlank()) {
            throw IllegalArgumentException(
                "El correo es obligatorio"
            )
        }

        if (request.password.isBlank()) {
            throw IllegalArgumentException(
                "La contraseña es obligatoria"
            )
        }

        val usuario =
            UsuarioRepository
                .obtenerPorCorreo(correoNormalizado)
                ?: throw IllegalArgumentException(
                    "Correo o contraseña incorrectos"
                )

        if (!usuario.activo) {
            throw IllegalArgumentException(
                "El usuario está inactivo"
            )
        }

        val passwordCorrecta =
            BCrypt.checkpw(
                request.password,
                usuario.passwordHash
            )

        if (!passwordCorrecta) {
            throw IllegalArgumentException(
                "Correo o contraseña incorrectos"
            )
        }

        /*
         * Los usuarios creados por el administrador
         * quedan verificados automáticamente.
         *
         * Los ciudadanos registrados públicamente
         * deben verificar primero su correo.
         */
        if (!usuario.correoVerificado) {
            throw IllegalArgumentException(
                "Debes verificar tu correo antes de iniciar sesión"
            )
        }

        val token =
            JwtConfig.generarToken(
                usuarioId = usuario.id,
                correo = usuario.correo,
                rol = usuario.rol
            )

        return LoginResponse(
            mensaje = "Inicio de sesión correcto",
            token = token,
            usuario = usuarioAResponse(usuario)
        )
    }

    fun registrarCiudadano(
        request: RegistroCiudadanoRequest
    ): RegistroCiudadanoResponse {

        val nombre =
            request.nombre.trim()

        val correo =
            request.correo
                .trim()
                .lowercase()

        val password =
            request.password

        val telefono =
            request.telefono
                ?.trim()
                ?.ifBlank { null }

        val municipio =
            request.municipio
                ?.trim()
                ?.ifBlank { null }

        validarRegistroCiudadano(
            nombre = nombre,
            correo = correo,
            password = password,
            telefono = telefono,
            municipio = municipio
        )

        val usuarioExistente =
            UsuarioRepository.obtenerPorCorreo(correo)

        if (usuarioExistente != null) {
            throw IllegalArgumentException(
                "El correo ya está registrado"
            )
        }

        val passwordHash =
            BCrypt.hashpw(
                password,
                BCrypt.gensalt()
            )

        val codigoVerificacion =
            generarCodigoVerificacion()

        val codigoExpiracion =
            LocalDateTime.now()
                .plusMinutes(
                    DURACION_CODIGO_MINUTOS
                )

        val ciudadano =
            UsuarioRepository.crearCiudadano(
                nombre = nombre,
                correo = correo,
                passwordHash = passwordHash,
                telefono = telefono,
                municipio = municipio,
                codigoVerificacion =
                    codigoVerificacion,
                codigoExpiracion =
                    codigoExpiracion
            )

        /*
         * Esta función se creará en el siguiente paso.
         * Enviará el código al correo real del ciudadano.
         */
        CorreoService.enviarCodigoVerificacion(
            destinatario = ciudadano.correo,
            nombre = ciudadano.nombre,
            codigo = codigoVerificacion
        )

        return RegistroCiudadanoResponse(
            mensaje =
                "Cuenta creada. Revisa tu correo para verificarla",
            usuario = usuarioAResponse(ciudadano)
        )
    }

    fun verificarCorreo(
        correo: String,
        codigo: String
    ): UsuarioResponse {

        val correoNormalizado =
            correo
                .trim()
                .lowercase()

        val codigoNormalizado =
            codigo.trim()

        if (
            correoNormalizado.isBlank() ||
            !correoNormalizado.contains("@")
        ) {
            throw IllegalArgumentException(
                "El correo no es válido"
            )
        }

        if (
            codigoNormalizado.length != 6 ||
            !codigoNormalizado.all(Char::isDigit)
        ) {
            throw IllegalArgumentException(
                "El código debe tener 6 dígitos"
            )
        }

        val usuario =
            UsuarioRepository
                .obtenerPorCorreo(correoNormalizado)
                ?: throw IllegalArgumentException(
                    "No existe una cuenta con ese correo"
                )

        if (usuario.correoVerificado) {
            throw IllegalArgumentException(
                "El correo ya fue verificado"
            )
        }

        val codigoGuardado =
            usuario.codigoVerificacion
                ?: throw IllegalArgumentException(
                    "No existe un código de verificación activo"
                )

        val expiracion =
            usuario.codigoExpiracion
                ?: throw IllegalArgumentException(
                    "El código de verificación no es válido"
                )

        if (LocalDateTime.now().isAfter(expiracion)) {
            throw IllegalArgumentException(
                "El código de verificación expiró"
            )
        }

        if (codigoGuardado != codigoNormalizado) {
            throw IllegalArgumentException(
                "El código de verificación es incorrecto"
            )
        }

        val usuarioVerificado =
            UsuarioRepository
                .marcarCorreoVerificado(usuario.id)
                ?: throw IllegalStateException(
                    "No fue posible verificar el correo"
                )

        return usuarioAResponse(usuarioVerificado)
    }

    fun reenviarCodigoVerificacion(
        correo: String
    ) {
        val correoNormalizado =
            correo
                .trim()
                .lowercase()

        val usuario =
            UsuarioRepository
                .obtenerPorCorreo(correoNormalizado)
                ?: throw IllegalArgumentException(
                    "No existe una cuenta con ese correo"
                )

        if (usuario.correoVerificado) {
            throw IllegalArgumentException(
                "El correo ya fue verificado"
            )
        }

        val nuevoCodigo =
            generarCodigoVerificacion()

        val nuevaExpiracion =
            LocalDateTime.now()
                .plusMinutes(
                    DURACION_CODIGO_MINUTOS
                )

        UsuarioRepository
            .actualizarCodigoVerificacion(
                usuarioId = usuario.id,
                codigo = nuevoCodigo,
                expiracion = nuevaExpiracion
            )
            ?: throw IllegalStateException(
                "No fue posible actualizar el código"
            )

        CorreoService.enviarCodigoVerificacion(
            destinatario = usuario.correo,
            nombre = usuario.nombre,
            codigo = nuevoCodigo
        )
    }

    private fun validarRegistroCiudadano(
        nombre: String,
        correo: String,
        password: String,
        telefono: String?,
        municipio: String?
    ) {
        when {
            nombre.isBlank() -> {
                throw IllegalArgumentException(
                    "El nombre es obligatorio"
                )
            }

            correo.isBlank() ||
                    !correo.contains("@") ||
                    !correo.substringAfter("@")
                        .contains(".") -> {
                throw IllegalArgumentException(
                    "El correo no es válido"
                )
            }

            password.length < 8 -> {
                throw IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres"
                )
            }

            telefono == null -> {
                throw IllegalArgumentException(
                    "El número telefónico es obligatorio"
                )
            }

            telefono.length != 10 ||
                    !telefono.all(Char::isDigit) -> {
                throw IllegalArgumentException(
                    "El teléfono debe tener 10 dígitos"
                )
            }

            municipio == null -> {
                throw IllegalArgumentException(
                    "El municipio o localidad es obligatorio"
                )
            }
        }
    }

    private fun generarCodigoVerificacion(): String {
        val numero =
            secureRandom.nextInt(900_000) +
                    100_000

        return numero.toString()
    }

    private fun usuarioAResponse(
        usuario: Usuario
    ): UsuarioResponse {
        return UsuarioResponse(
            id = usuario.id,
            nombre = usuario.nombre,
            correo = usuario.correo,
            rol = usuario.rol,
            area = usuario.area,
            telefono = usuario.telefono,
            activo = usuario.activo
        )
    }
}
