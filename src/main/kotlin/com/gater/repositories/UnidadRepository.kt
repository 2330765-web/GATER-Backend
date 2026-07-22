package com.gater.repositories

import com.gater.database.DatabaseFactory
import com.gater.database.EstadoUnidad
import com.gater.database.UnidadesTable
import com.gater.dto.ActualizarUnidadRequest
import com.gater.dto.CrearUnidadRequest
import com.gater.models.Unidad
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object UnidadRepository {

    fun crear(request: CrearUnidadRequest): Unidad =
        transaction(DatabaseFactory.database) {

            val nuevoId = UnidadesTable.insert { statement ->
                statement[UnidadesTable.numero] =
                    request.numero.trim()

                statement[UnidadesTable.tipo] =
                    request.tipo.trim()

                statement[UnidadesTable.placas] =
                    request.placas?.trim()

                statement[UnidadesTable.estado] =
                    EstadoUnidad.valueOf(
                        request.estado.trim().uppercase()
                    )

                statement[UnidadesTable.operador] =
                    request.operador?.trim()

                statement[UnidadesTable.capacidad] =
                    request.capacidad

                statement[UnidadesTable.activo] =
                    request.activo
            } get UnidadesTable.id

            obtenerPorIdInterno(nuevoId)
                ?: throw IllegalStateException(
                    "No fue posible recuperar la unidad creada"
                )
        }

    fun listar(): List<Unidad> =
        transaction(DatabaseFactory.database) {
            UnidadesTable
                .selectAll()
                .map(::filaAUnidad)
        }

    fun obtenerPorId(id: Int): Unidad? =
        transaction(DatabaseFactory.database) {
            obtenerPorIdInterno(id)
        }

    fun obtenerPorNumero(numero: String): Unidad? =
        transaction(DatabaseFactory.database) {
            UnidadesTable
                .selectAll()
                .where {
                    UnidadesTable.numero eq numero.trim()
                }
                .limit(1)
                .map(::filaAUnidad)
                .singleOrNull()
        }

    fun actualizar(
        id: Int,
        request: ActualizarUnidadRequest
    ): Unidad? =
        transaction(DatabaseFactory.database) {

            val filasActualizadas =
                UnidadesTable.update(
                    where = {
                        UnidadesTable.id eq id
                    }
                ) { statement ->

                    request.numero?.let {
                        statement[UnidadesTable.numero] =
                            it.trim()
                    }

                    request.tipo?.let {
                        statement[UnidadesTable.tipo] =
                            it.trim()
                    }

                    request.placas?.let {
                        statement[UnidadesTable.placas] =
                            it.trim()
                    }

                    request.estado?.let {
                        statement[UnidadesTable.estado] =
                            EstadoUnidad.valueOf(
                                it.trim().uppercase()
                            )
                    }

                    request.operador?.let {
                        statement[UnidadesTable.operador] =
                            it.trim()
                    }

                    request.capacidad?.let {
                        statement[UnidadesTable.capacidad] =
                            it
                    }

                    request.activo?.let {
                        statement[UnidadesTable.activo] =
                            it
                    }
                }

            if (filasActualizadas == 0) {
                null
            } else {
                obtenerPorIdInterno(id)
            }
        }

    fun eliminar(id: Int): Boolean =
        transaction(DatabaseFactory.database) {
            UnidadesTable.deleteWhere {
                UnidadesTable.id eq id
            } > 0
        }

    private fun obtenerPorIdInterno(id: Int): Unidad? {
        return UnidadesTable
            .selectAll()
            .where {
                UnidadesTable.id eq id
            }
            .limit(1)
            .map(::filaAUnidad)
            .singleOrNull()
    }

    private fun filaAUnidad(fila: ResultRow): Unidad {
        return Unidad(
            id = fila[UnidadesTable.id],
            numero = fila[UnidadesTable.numero],
            tipo = fila[UnidadesTable.tipo],
            placas = fila[UnidadesTable.placas],
            estado = fila[UnidadesTable.estado].name,
            operador = fila[UnidadesTable.operador],
            capacidad = fila[UnidadesTable.capacidad],
            activo = fila[UnidadesTable.activo]
        )
    }
}