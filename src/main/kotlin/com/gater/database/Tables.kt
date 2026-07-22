package com.gater.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.datetime
import org.jetbrains.exposed.v1.javatime.time

object UsuariosTable : Table("usuarios") {
    val id = integer("id").autoIncrement()

    val nombre = varchar("nombre", 120)
    val correo = varchar("correo", 150).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)

    val rol = enumerationByName<RolUsuario>("rol", 30)
    val area = varchar("area", 100).nullable()
    val telefono = varchar("telefono", 20).nullable()

    val activo = bool("activo").default(true)
    val fechaCreacion = datetime("fecha_creacion")

    override val primaryKey = PrimaryKey(id)
}

object HospitalesTable : Table("hospitales") {
    val id = integer("id").autoIncrement()

    val nombre = varchar("nombre", 150)
    val direccion = varchar("direccion", 255)
    val municipio = varchar("municipio", 100)
    val estado = varchar("estado", 100)

    val especialidad = varchar("especialidad", 150).nullable()
    val telefono = varchar("telefono", 20).nullable()
    val tipo = varchar("tipo", 80).nullable()

    val activo = bool("activo").default(true)

    override val primaryKey = PrimaryKey(id)
}

object UnidadesTable : Table("unidades") {
    val id = integer("id").autoIncrement()

    val numero = varchar("numero", 50).uniqueIndex()
    val tipo = varchar("tipo", 80)
    val placas = varchar("placas", 30).nullable()

    val estado = enumerationByName<EstadoUnidad>("estado", 30)
    val operador = varchar("operador", 120).nullable()
    val capacidad = integer("capacidad").nullable()

    val activo = bool("activo").default(true)

    override val primaryKey = PrimaryKey(id)
}

object ReportesTable : Table("reportes") {
    val id = integer("id").autoIncrement()

    val usuarioId = integer("usuario_id")
        .references(UsuariosTable.id)
        .nullable()

    val tipoEmergencia = varchar("tipo_emergencia", 100)
    val descripcion = text("descripcion")

    val nivelEmergencia =
        enumerationByName<NivelEmergencia>("nivel_emergencia", 20)

    val latitud = decimal("latitud", precision = 10, scale = 7)
    val longitud = decimal("longitud", precision = 10, scale = 7)

    val direccionReferencia =
        varchar("direccion_referencia", 255).nullable()

    val estado =
        enumerationByName<EstadoReporte>("estado", 30)

    val fechaCreacion = datetime("fecha_creacion")

    override val primaryKey = PrimaryKey(id)
}

object TrasladosTable : Table("traslados") {
    val id = integer("id").autoIncrement()

    val paciente = varchar("paciente", 150)
    val edad = integer("edad")
    val telefono = varchar("telefono", 20).nullable()

    val hospitalId = integer("hospital_id")
        .references(HospitalesTable.id)

    val unidadId = integer("unidad_id")
        .references(UnidadesTable.id)
        .nullable()

    val bomberoId = integer("bombero_id")
        .references(UsuariosTable.id)
        .nullable()

    val fecha = date("fecha")
    val hora = time("hora")

    val requiereOxigeno =
        bool("requiere_oxigeno").default(false)

    val requiereParamedico =
        bool("requiere_paramedico").default(false)

    val estado =
        enumerationByName<EstadoTraslado>("estado", 30)

    val observaciones = text("observaciones").nullable()

    override val primaryKey = PrimaryKey(id)
}

enum class RolUsuario {
    ADMINISTRADOR,
    COORDINADOR,
    BOMBERO,
    CIUDADANO
}

enum class EstadoUnidad {
    DISPONIBLE,
    OCUPADA,
    MANTENIMIENTO,
    FUERA_DE_SERVICIO
}

enum class NivelEmergencia {
    ROJO,
    AMARILLO,
    VERDE
}

enum class EstadoReporte {
    PENDIENTE,
    ASIGNADO,
    EN_ATENCION,
    ATENDIDO,
    CANCELADO
}

enum class EstadoTraslado {
    PROGRAMADO,
    CONFIRMADO,
    EN_CURSO,
    FINALIZADO,
    CANCELADO
}