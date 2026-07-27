package com.gater.services

import com.gater.dto.LoginRequest
import com.gater.dto.LoginResponse
import com.gater.dto.RegistroCiudadanoRequest
import com.gater.dto.RegistroCiudadanoResponse
import com.gater.dto.UsuarioResponse
import com.gater.repositories.UsuarioRepository
import com.gater.security.JwtConfig
import org.mindrot.jbcrypt.BCrypt

object AuthService {

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

        when {
            nombre.isBlank() -> {
                throw IllegalArgumentException(
                    "El nombre es obligatorio"
                )
            }

            correo.isBlank() ||
                    !correo.contains("@") -> {
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

        val ciudadano =
            UsuarioRepository.crearCiudadano(
                nombre = nombre,
                correo = correo,
                passwordHash = passwordHash,
                telefono = telefono,
                municipio = municipio
            )

        return RegistroCiudadanoResponse(
            mensaje = "Cuenta creada correctamente",
            usuario = usuarioAResponse(ciudadano)
        )
    }

    private fun usuarioAResponse(
        usuario: com.gater.models.Usuario
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
