package com.gater.repositories

import com.gater.database.DatabaseFactory
import com.gater.database.EstadoTraslado
import com.gater.database.TrasladosTable
import com.gater.dto.ActualizarTrasladoRequest
import com.gater.dto.CrearTrasladoRequest
import com.gater.models.Traslado
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDate
import java.time.LocalTime

object TrasladoRepository {

    fun crear(
        request: CrearTrasladoRequest
    ): Traslado =
        transaction(DatabaseFactory.database) {

            val nuevoId = TrasladosTable.insert { statement ->

                statement[TrasladosTable.paciente] =
                    request.paciente.trim()

                statement[TrasladosTable.edad] =
                    request.edad

                statement[TrasladosTable.telefono] =
                    request.telefono?.trim()

                statement[TrasladosTable.hospitalId] =
                    request.hospitalId

                statement[TrasladosTable.unidadId] =
                    request.unidadId

                statement[TrasladosTable.bomberoId] =
                    request.bomberoId

                statement[TrasladosTable.fecha] =
                    LocalDate.parse(request.fecha)

                statement[TrasladosTable.hora] =
                    LocalTime.parse(request.hora)

                statement[TrasladosTable.requiereOxigeno] =
                    request.requiereOxigeno

                statement[TrasladosTable.requiereParamedico] =
                    request.requiereParamedico

                statement[TrasladosTable.estado] =
                    EstadoTraslado.valueOf(
                        request.estado.trim().uppercase()
                    )

                statement[TrasladosTable.observaciones] =
                    request.observaciones?.trim()

            } get TrasladosTable.id

            obtenerPorIdInterno(nuevoId)
                ?: throw IllegalStateException(
                    "No fue posible recuperar el traslado creado"
                )
        }

    fun listar(): List<Traslado> =
        transaction(DatabaseFactory.database) {

            TrasladosTable
                .selectAll()
                .map(::filaATraslado)
        }

    fun obtenerPorId(
        id: Int
    ): Traslado? =
        transaction(DatabaseFactory.database) {

            obtenerPorIdInterno(id)
        }

    fun actualizar(
        id: Int,
        request: ActualizarTrasladoRequest
    ): Traslado? =
        transaction(DatabaseFactory.database) {

            val filasActualizadas =
                TrasladosTable.update(
                    where = {
                        TrasladosTable.id eq id
                    }
                ) { statement ->

                    request.paciente?.let {
                        statement[TrasladosTable.paciente] =
                            it.trim()
                    }

                    request.edad?.let {
                        statement[TrasladosTable.edad] =
                            it
                    }

                    request.telefono?.let {
                        statement[TrasladosTable.telefono] =
                            it.trim()
                    }

                    request.hospitalId?.let {
                        statement[TrasladosTable.hospitalId] =
                            it
                    }

                    request.unidadId?.let {
                        statement[TrasladosTable.unidadId] =
                            it
                    }

                    request.bomberoId?.let {
                        statement[TrasladosTable.bomberoId] =
                            it
                    }

                    request.fecha?.let {
                        statement[TrasladosTable.fecha] =
                            LocalDate.parse(it)
                    }

                    request.hora?.let {
                        statement[TrasladosTable.hora] =
                            LocalTime.parse(it)
                    }

                    request.requiereOxigeno?.let {
                        statement[TrasladosTable.requiereOxigeno] =
                            it
                    }

                    request.requiereParamedico?.let {
                        statement[TrasladosTable.requiereParamedico] =
                            it
                    }

                    request.estado?.let {
                        statement[TrasladosTable.estado] =
                            EstadoTraslado.valueOf(
                                it.trim().uppercase()
                            )
                    }

                    request.observaciones?.let {
                        statement[TrasladosTable.observaciones] =
                            it.trim()
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

            TrasladosTable.deleteWhere {
                TrasladosTable.id eq id
            } > 0
        }

    private fun obtenerPorIdInterno(
        id: Int
    ): Traslado? {

        return TrasladosTable
            .selectAll()
            .where {
                TrasladosTable.id eq id
            }
            .limit(1)
            .map(::filaATraslado)
            .singleOrNull()
    }

    private fun filaATraslado(
        fila: ResultRow
    ): Traslado {

        return Traslado(
            id = fila[TrasladosTable.id],
            paciente = fila[TrasladosTable.paciente],
            edad = fila[TrasladosTable.edad],
            telefono = fila[TrasladosTable.telefono],
            hospitalId = fila[TrasladosTable.hospitalId],
            unidadId = fila[TrasladosTable.unidadId],
            bomberoId = fila[TrasladosTable.bomberoId],
            fecha = fila[TrasladosTable.fecha],
            hora = fila[TrasladosTable.hora],
            requiereOxigeno =
                fila[TrasladosTable.requiereOxigeno],
            requiereParamedico =
                fila[TrasladosTable.requiereParamedico],
            estado = fila[TrasladosTable.estado].name,
            observaciones =
                fila[TrasladosTable.observaciones]
        )
    }
}