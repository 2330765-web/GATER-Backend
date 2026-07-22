package com.gater.services

import com.gater.database.RolUsuario
import com.gater.dto.ActualizarUsuarioRequest
import com.gater.dto.CrearUsuarioRequest
import com.gater.dto.UsuarioResponse
import com.gater.repositories.UsuarioRepository
import org.mindrot.jbcrypt.BCrypt

object UsuarioService {

    fun crear(request: CrearUsuarioRequest): UsuarioResponse {
        validarCrearUsuario(request)

        val usuarioExistente =
            UsuarioRepository.obtenerPorCorreo(request.correo)

        if (usuarioExistente != null) {
            throw IllegalArgumentException(
                "Ya existe un usuario registrado con ese correo"
            )
        }

        val passwordHash = BCrypt.hashpw(
            request.password,
            BCrypt.gensalt(12)
        )

        val usuario = UsuarioRepository.crear(
            request = request,
            passwordHash = passwordHash
        )

        return usuario.toResponse()
    }

    fun listar(): List<UsuarioResponse> {
        return UsuarioRepository
            .listar()
            .map { usuario -> usuario.toResponse() }
    }

    fun obtenerPorId(id: Int): UsuarioResponse? {
        return UsuarioRepository
            .obtenerPorId(id)
            ?.toResponse()
    }

    fun actualizar(
        id: Int,
        request: ActualizarUsuarioRequest
    ): UsuarioResponse? {
        validarActualizarUsuario(request)

        return UsuarioRepository
            .actualizar(id, request)
            ?.toResponse()
    }

    fun eliminar(id: Int): Boolean {
        return UsuarioRepository.eliminar(id)
    }

    private fun validarCrearUsuario(
        request: CrearUsuarioRequest
    ) {
        if (request.nombre.isBlank()) {
            throw IllegalArgumentException(
                "El nombre es obligatorio"
            )
        }

        if (
            request.correo.isBlank() ||
            !request.correo.contains("@")
        ) {
            throw IllegalArgumentException(
                "El correo electrónico no es válido"
            )
        }

        if (request.password.length < 8) {
            throw IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres"
            )
        }

        try {
            RolUsuario.valueOf(
                request.rol.trim().uppercase()
            )
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException(
                "El rol enviado no es válido"
            )
        }
    }

    private fun validarActualizarUsuario(
        request: ActualizarUsuarioRequest
    ) {
        request.nombre?.let { nombre ->
            if (nombre.isBlank()) {
                throw IllegalArgumentException(
                    "El nombre no puede quedar vacío"
                )
            }
        }

        request.rol?.let { rol ->
            try {
                RolUsuario.valueOf(
                    rol.trim().uppercase()
                )
            } catch (_: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "El rol enviado no es válido"
                )
            }
        }
    }

    private fun com.gater.models.Usuario.toResponse(): UsuarioResponse {
        return UsuarioResponse(
            id = id,
            nombre = nombre,
            correo = correo,
            rol = rol,
            area = area,
            telefono = telefono,
            activo = activo
        )
    }
}