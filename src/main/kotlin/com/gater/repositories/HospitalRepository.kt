package com.gater.repositories

import com.gater.database.DatabaseFactory
import com.gater.database.HospitalesTable
import com.gater.dto.ActualizarHospitalRequest
import com.gater.dto.CrearHospitalRequest
import com.gater.models.Hospital
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object HospitalRepository {

    fun crear(request: CrearHospitalRequest): Hospital =
        transaction(DatabaseFactory.database) {

            val nuevoId = HospitalesTable.insert { statement ->
                statement[HospitalesTable.nombre] =
                    request.nombre.trim()

                statement[HospitalesTable.direccion] =
                    request.direccion.trim()

                statement[HospitalesTable.municipio] =
                    request.municipio.trim()

                statement[HospitalesTable.estado] =
                    request.estado.trim()

                statement[HospitalesTable.especialidad] =
                    request.especialidad?.trim()

                statement[HospitalesTable.telefono] =
                    request.telefono?.trim()

                statement[HospitalesTable.tipo] =
                    request.tipo.trim()

                statement[HospitalesTable.activo] = true
            } get HospitalesTable.id

            obtenerPorIdInterno(nuevoId)
                ?: throw IllegalStateException(
                    "No fue posible recuperar el hospital creado"
                )
        }

    fun listar(): List<Hospital> =
        transaction(DatabaseFactory.database) {
            HospitalesTable
                .selectAll()
                .map(::filaAHospital)
        }

    fun obtenerPorId(id: Int): Hospital? =
        transaction(DatabaseFactory.database) {
            obtenerPorIdInterno(id)
        }

    fun actualizar(
        id: Int,
        request: ActualizarHospitalRequest
    ): Hospital? =
        transaction(DatabaseFactory.database) {

            val filasActualizadas = HospitalesTable.update(
                where = {
                    HospitalesTable.id eq id
                }
            ) { statement ->

                request.nombre?.let {
                    statement[HospitalesTable.nombre] = it.trim()
                }

                request.direccion?.let {
                    statement[HospitalesTable.direccion] = it.trim()
                }

                request.municipio?.let {
                    statement[HospitalesTable.municipio] = it.trim()
                }

                request.estado?.let {
                    statement[HospitalesTable.estado] = it.trim()
                }

                request.especialidad?.let {
                    statement[HospitalesTable.especialidad] = it.trim()
                }

                request.telefono?.let {
                    statement[HospitalesTable.telefono] = it.trim()
                }

                request.tipo?.let {
                    statement[HospitalesTable.tipo] = it.trim()
                }

                request.activo?.let {
                    statement[HospitalesTable.activo] = it
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
            HospitalesTable.deleteWhere {
                HospitalesTable.id eq id
            } > 0
        }

    private fun obtenerPorIdInterno(id: Int): Hospital? {
        return HospitalesTable
            .selectAll()
            .where {
                HospitalesTable.id eq id
            }
            .limit(1)
            .map(::filaAHospital)
            .singleOrNull()
    }

    private fun filaAHospital(fila: ResultRow): Hospital {
        return Hospital(
            id = fila[HospitalesTable.id],
            nombre = fila[HospitalesTable.nombre],
            direccion = fila[HospitalesTable.direccion],
            municipio = fila[HospitalesTable.municipio],
            estado = fila[HospitalesTable.estado],
            especialidad = fila[HospitalesTable.especialidad],
            telefono = fila[HospitalesTable.telefono],
            tipo = fila[HospitalesTable.tipo] ?: "",
            activo = fila[HospitalesTable.activo]
        )
    }
}