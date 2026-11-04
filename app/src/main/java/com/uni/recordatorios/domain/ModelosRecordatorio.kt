package com.uni.recordatorios.domain

data class Categoria(
    val id: String,
    val nombre: String,
    val descripcion: String
)

data class EstiloCategoria(
    val etiqueta: String,
    val colorHex: Long,
    val colorFondoHex: Long
)

sealed class Prioridad(
    val codigo: String,
    val nombreVisible: String,
    val ordenImportancia: Int
) {
    data object Alta : Prioridad(
        codigo = "ALTA",
        nombreVisible = "Prioridad Alta",
        ordenImportancia = 3
    )

    data object Media : Prioridad(
        codigo = "MEDIA",
        nombreVisible = "Prioridad Media",
        ordenImportancia = 2
    )

    data object Baja : Prioridad(
        codigo = "BAJA",
        nombreVisible = "Prioridad Baja",
        ordenImportancia = 1
    )

    companion object {
        fun todas(): List<Prioridad> = listOf(Alta, Media, Baja)

        fun desdeCodigo(codigo: String): Prioridad = when (codigo.uppercase()) {
            "ALTA" -> Alta
            "MEDIA" -> Media
            else -> Baja
        }
    }
}

sealed class EstadoRecordatorio {
    data object Pendiente : EstadoRecordatorio()

    data class EnProgreso(
        val porcentajeAvance: Int
    ) : EstadoRecordatorio()

    data class Completado(
        val fechaFinalizacion: String
    ) : EstadoRecordatorio()
}

data class Recordatorio(
    val id: Long,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    val ubicacionOpcional: String? = null,
    val avisoOpcional: String? = null,
    val notaOpcional: String? = null,
    val sincronizadoNube: Boolean = false
)

fun Recordatorio.estaCompletado(): Boolean =
    estado is EstadoRecordatorio.Completado

fun Recordatorio.estaPendiente(): Boolean =
    estado is EstadoRecordatorio.Pendiente

fun Recordatorio.alternarEstadoCompletado(fechaActual: String = "Hoy, 18:30 hs"): Recordatorio =
    when (estado) {
        is EstadoRecordatorio.Completado -> copy(estado = EstadoRecordatorio.Pendiente)
        else -> copy(estado = EstadoRecordatorio.Completado(fechaFinalizacion = fechaActual))
    }

fun Recordatorio.obtenerUbicacionVisible(): String =
    ubicacionOpcional
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Sin ubicacion asignada"

fun Recordatorio.obtenerAvisoVisible(): String =
    avisoOpcional
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Sin aviso programado"

fun Recordatorio.obtenerNotaVisible(): String =
    notaOpcional
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "Sin observaciones"

fun EstadoRecordatorio.textoDescriptivo(): String = when (this) {
    is EstadoRecordatorio.Pendiente -> "Pendiente"
    is EstadoRecordatorio.EnProgreso -> "En curso (${porcentajeAvance}%)"
    is EstadoRecordatorio.Completado -> "Completado ($fechaFinalizacion)"
}

sealed class EstadoPantallaRecordatorios {
    data object Inicial : EstadoPantallaRecordatorios()
    data object Cargando : EstadoPantallaRecordatorios()
    data class Exito(val recordatorios: List<Recordatorio>) : EstadoPantallaRecordatorios()
    data object SinResultados : EstadoPantallaRecordatorios()
    data class Error(val mensaje: String) : EstadoPantallaRecordatorios()
}

sealed class ResultadoGuardadoRecordatorio {
    data object Guardando : ResultadoGuardadoRecordatorio()

    data class Exito(
        val recordatorio: Recordatorio
    ) : ResultadoGuardadoRecordatorio()

    data class ErrorValidacion(
        val camposInvalidos: List<String>,
        val mensaje: String
    ) : ResultadoGuardadoRecordatorio()

    data object SinCambios : ResultadoGuardadoRecordatorio()
}
