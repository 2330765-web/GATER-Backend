package com.gater.repositories

import com.gater.database.DatabaseFactory
import com.gater.database.RolUsuario
import com.gater.database.UsuariosTable
import com.gater.dto.ActualizarUsuarioRequest
import com.gater.dto.CrearUsuarioRequest
import com.gater.models.Usuario
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime

object UsuarioRepository {

    fun crear(
        request: CrearUsuarioRequest,
        passwordHash: String
    ): Usuario = transaction(DatabaseFactory.database) {

        val correoNormalizado =
            request.correo
                .trim()
                .lowercase()

        val usuarioExistente =
            UsuariosTable
                .selectAll()
                .where {
                    UsuariosTable.correo eq correoNormalizado
                }
                .limit(1)
                .singleOrNull()

        if (usuarioExistente != null) {
            throw IllegalArgumentException(
                "El correo ya está registrado"
            )
        }

        val nuevoId =
            UsuariosTable.insert { statement ->

                statement[UsuariosTable.nombre] =
                    request.nombre.trim()

                statement[UsuariosTable.correo] =
                    correoNormalizado

                statement[UsuariosTable.passwordHash] =
                    passwordHash

                statement[UsuariosTable.rol] =
                    RolUsuario.valueOf(
                        request.rol
                            .trim()
                            .uppercase()
                    )

                statement[UsuariosTable.area] =
                    request.area
                        ?.trim()
                        ?.ifBlank { null }

                statement[UsuariosTable.telefono] =
                    request.telefono
                        ?.trim()
                        ?.ifBlank { null }

                statement[UsuariosTable.activo] =
                    true

                statement[UsuariosTable.fechaCreacion] =
                    LocalDateTime.now()
            } get UsuariosTable.id

        obtenerPorIdInterno(nuevoId)
            ?: throw IllegalStateException(
                "No fue posible recuperar el usuario creado"
            )
    }

    fun crearCiudadano(
        nombre: String,
        correo: String,
        passwordHash: String,
        telefono: String?,
        municipio: String?
    ): Usuario = transaction(DatabaseFactory.database) {

        val correoNormalizado =
            correo
                .trim()
                .lowercase()

        val usuarioExistente =
            UsuariosTable
                .selectAll()
                .where {
                    UsuariosTable.correo eq correoNormalizado
                }
                .limit(1)
                .singleOrNull()

        if (usuarioExistente != null) {
            throw IllegalArgumentException(
                "El correo ya está registrado"
            )
        }

        val nuevoId =
            UsuariosTable.insert { statement ->

                statement[UsuariosTable.nombre] =
                    nombre.trim()

                statement[UsuariosTable.correo] =
                    correoNormalizado

                statement[UsuariosTable.passwordHash] =
                    passwordHash

                statement[UsuariosTable.rol] =
                    RolUsuario.CIUDADANO

                // Por ahora guardamos el municipio
                // en el campo área.
                statement[UsuariosTable.area] =
                    municipio
                        ?.trim()
                        ?.ifBlank { null }

                statement[UsuariosTable.telefono] =
                    telefono
                        ?.trim()
                        ?.ifBlank { null }

                statement[UsuariosTable.activo] =
                    true

                statement[UsuariosTable.fechaCreacion] =
                    LocalDateTime.now()
            } get UsuariosTable.id

        obtenerPorIdInterno(nuevoId)
            ?: throw IllegalStateException(
                "No fue posible recuperar el ciudadano creado"
            )
    }

    fun listar(): List<Usuario> =
        transaction(DatabaseFactory.database) {
            UsuariosTable
                .selectAll()
                .map(::filaAUsuario)
        }

    fun obtenerPorId(
        id: Int
    ): Usuario? =
        transaction(DatabaseFactory.database) {
            obtenerPorIdInterno(id)
        }

    fun obtenerPorCorreo(
        correo: String
    ): Usuario? =
        transaction(DatabaseFactory.database) {

            UsuariosTable
                .selectAll()
                .where {
                    UsuariosTable.correo eq
                            correo
                                .trim()
                                .lowercase()
                }
                .limit(1)
                .map(::filaAUsuario)
                .singleOrNull()
        }

    fun actualizar(
        id: Int,
        request: ActualizarUsuarioRequest
    ): Usuario? =
        transaction(DatabaseFactory.database) {

            val filasActualizadas =
                UsuariosTable.update(
                    where = {
                        UsuariosTable.id eq id
                    }
                ) { statement ->

                    request.nombre?.let { nuevoNombre ->
                        statement[UsuariosTable.nombre] =
                            nuevoNombre.trim()
                    }

                    request.rol?.let { nuevoRol ->
                        statement[UsuariosTable.rol] =
                            RolUsuario.valueOf(
                                nuevoRol
                                    .trim()
                                    .uppercase()
                            )
                    }

                    request.area?.let { nuevaArea ->
                        statement[UsuariosTable.area] =
                            nuevaArea
                                .trim()
                                .ifBlank { null }
                    }

                    request.telefono?.let { nuevoTelefono ->
                        statement[UsuariosTable.telefono] =
                            nuevoTelefono
                                .trim()
                                .ifBlank { null }
                    }

                    request.activo?.let { nuevoEstado ->
                        statement[UsuariosTable.activo] =
                            nuevoEstado
                    }
                }

            if (filasActualizadas == 0) {
                null
            } else {
                obtenerPorIdInterno(id)
            }
        }

    fun eliminar(
        id: Int
    ): Boolean =
        transaction(DatabaseFactory.database) {
            UsuariosTable.deleteWhere {
                UsuariosTable.id eq id
            } > 0
        }

    private fun obtenerPorIdInterno(
        id: Int
    ): Usuario? {
        return UsuariosTable
            .selectAll()
            .where {
                UsuariosTable.id eq id
            }
            .limit(1)
            .map(::filaAUsuario)
            .singleOrNull()
    }

    private fun filaAUsuario(
        fila: ResultRow
    ): Usuario {
        return Usuario(
            id = fila[UsuariosTable.id],
            nombre = fila[UsuariosTable.nombre],
            correo = fila[UsuariosTable.correo],
            passwordHash =
                fila[UsuariosTable.passwordHash],
            rol = fila[UsuariosTable.rol].name,
            area = fila[UsuariosTable.area],
            telefono = fila[UsuariosTable.telefono],
            activo = fila[UsuariosTable.activo]
        )
    }
}
