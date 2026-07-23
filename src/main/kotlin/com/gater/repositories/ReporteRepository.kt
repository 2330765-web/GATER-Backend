package com.gater.repositories

import com.gater.database.DatabaseFactory
import com.gater.database.EstadoReporte
import com.gater.database.NivelEmergencia
import com.gater.database.ReportesTable
import com.gater.dto.ActualizarReporteRequest
import com.gater.dto.CrearReporteRequest
import com.gater.models.Reporte
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.math.BigDecimal
import java.time.LocalDateTime

object ReporteRepository {

    fun crear(request: CrearReporteRequest): Reporte =
        transaction(DatabaseFactory.database) {

            val nuevoId = ReportesTable.insert { statement ->

                statement[ReportesTable.usuarioId] =
                    request.usuarioId

                statement[ReportesTable.tipoEmergencia] =
                    request.tipoEmergencia.trim()

                statement[ReportesTable.descripcion] =
                    request.descripcion.trim()

                statement[ReportesTable.nivelEmergencia] =
                    NivelEmergencia.valueOf(
                        request.nivelEmergencia
                            .trim()
                            .uppercase()
                    )

                statement[ReportesTable.latitud] =
                    BigDecimal.valueOf(request.latitud)

                statement[ReportesTable.longitud] =
                    BigDecimal.valueOf(request.longitud)

                statement[ReportesTable.direccionReferencia] =
                    request.direccionReferencia?.trim()

                statement[ReportesTable.estado] =
                    EstadoReporte.PENDIENTE

                statement[ReportesTable.fechaCreacion] =
                    LocalDateTime.now()
            } get ReportesTable.id

            obtenerPorIdInterno(nuevoId)
                ?: throw IllegalStateException(
                    "No fue posible recuperar el reporte creado"
                )
        }

    fun listar(): List<Reporte> =
        transaction(DatabaseFactory.database) {

            ReportesTable
                .selectAll()
                .map(::filaAReporte)
        }

    fun obtenerPorId(id: Int): Reporte? =
        transaction(DatabaseFactory.database) {

            obtenerPorIdInterno(id)
        }

    fun actualizar(
        id: Int,
        request: ActualizarReporteRequest
    ): Reporte? =
        transaction(DatabaseFactory.database) {

            val filasActualizadas =
                ReportesTable.update(
                    where = {
                        ReportesTable.id eq id
                    }
                ) { statement ->

                    request.tipoEmergencia?.let {
                        statement[ReportesTable.tipoEmergencia] =
                            it.trim()
                    }

                    request.descripcion?.let {
                        statement[ReportesTable.descripcion] =
                            it.trim()
                    }

                    request.nivelEmergencia?.let {
                        statement[ReportesTable.nivelEmergencia] =
                            NivelEmergencia.valueOf(
                                it.trim().uppercase()
                            )
                    }

                    request.latitud?.let {
                        statement[ReportesTable.latitud] =
                            BigDecimal.valueOf(it)
                    }

                    request.longitud?.let {
                        statement[ReportesTable.longitud] =
                            BigDecimal.valueOf(it)
                    }

                    request.direccionReferencia?.let {
                        statement[ReportesTable.direccionReferencia] =
                            it.trim()
                    }

                    request.estado?.let {
                        statement[ReportesTable.estado] =
                            EstadoReporte.valueOf(
                                it.trim().uppercase()
                            )
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

            ReportesTable.deleteWhere {
                ReportesTable.id eq id
            } > 0
        }

    private fun obtenerPorIdInterno(id: Int): Reporte? {

        return ReportesTable
            .selectAll()
            .where {
                ReportesTable.id eq id
            }
            .limit(1)
            .map(::filaAReporte)
            .singleOrNull()
    }

    private fun filaAReporte(fila: ResultRow): Reporte {

        return Reporte(
            id = fila[ReportesTable.id],
            usuarioId = fila[ReportesTable.usuarioId],
            tipoEmergencia = fila[ReportesTable.tipoEmergencia],
            descripcion = fila[ReportesTable.descripcion],
            nivelEmergencia =
                fila[ReportesTable.nivelEmergencia].name,
            latitud =
                fila[ReportesTable.latitud].toDouble(),
            longitud =
                fila[ReportesTable.longitud].toDouble(),
            direccionReferencia =
                fila[ReportesTable.direccionReferencia],
            estado =
                fila[ReportesTable.estado].name,
            fechaCreacion =
                fila[ReportesTable.fechaCreacion]
        )
    }
}