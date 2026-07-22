package com.gater.services

import com.gater.dto.LoginRequest
import com.gater.dto.LoginResponse
import com.gater.dto.UsuarioResponse
import com.gater.repositories.UsuarioRepository
import org.mindrot.jbcrypt.BCrypt

object AuthService {

    fun login(request: LoginRequest): LoginResponse {

        val correoNormalizado = request.correo
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
            UsuarioRepository.obtenerPorCorreo(correoNormalizado)
                ?: throw IllegalArgumentException(
                    "Correo o contraseña incorrectos"
                )

        if (!usuario.activo) {
            throw IllegalArgumentException(
                "El usuario está inactivo"
            )
        }

        val passwordCorrecta = BCrypt.checkpw(
            request.password,
            usuario.passwordHash
        )

        if (!passwordCorrecta) {
            throw IllegalArgumentException(
                "Correo o contraseña incorrectos"
            )
        }

        return LoginResponse(
            mensaje = "Inicio de sesión correcto",
            usuario = UsuarioResponse(
                id = usuario.id,
                nombre = usuario.nombre,
                correo = usuario.correo,
                rol = usuario.rol,
                area = usuario.area,
                telefono = usuario.telefono,
                activo = usuario.activo
            )
        )
    }
}