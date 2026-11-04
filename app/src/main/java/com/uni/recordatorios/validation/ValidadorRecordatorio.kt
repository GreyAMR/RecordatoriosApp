package com.uni.recordatorios.validation

import com.uni.recordatorios.domain.Categoria
import com.uni.recordatorios.domain.EstadoRecordatorio
import com.uni.recordatorios.domain.Prioridad
import com.uni.recordatorios.domain.Recordatorio
import com.uni.recordatorios.domain.ResultadoGuardadoRecordatorio

object ValidadorRecordatorio {

    const val LONGITUD_MINIMA_TITULO = 4
    const val LONGITUD_MAXIMA_TITULO = 80

    fun validarTitulo(titulo: String): String? = when {
        titulo.isBlank() -> "El titulo es obligatorio"
        titulo.trim().length < LONGITUD_MINIMA_TITULO ->
            "Ingresa al menos cuatro caracteres"
        titulo.trim().length > LONGITUD_MAXIMA_TITULO ->
            "El titulo no puede superar los $LONGITUD_MAXIMA_TITULO caracteres"
        else -> null
    }

    fun esTituloValido(titulo: String): Boolean =
        validarTitulo(titulo) == null

    fun normalizarTextoOpcional(texto: String?): String? =
        texto?.trim()?.takeIf { it.isNotEmpty() }

    fun validarMinutosAnticipacion(
        textoMinutos: String,
        minimo: Int = 1,
        maximo: Int = 1440
    ): Int? {
        val valorConvertido = textoMinutos.trim().toIntOrNull() ?: return null
        return valorConvertido.takeIf { it in minimo..maximo }
    }

    fun calcularProporcionCompletados(completados: Int, total: Int): Double {
        require(total > 0) { "El total debe ser mayor que cero" }
        check(completados in 0..total) { "Cantidad invalida" }
        return completados.toDouble() / total
    }

    fun validarYCrearRecordatorio(
        idNuevo: Long,
        titulo: String,
        categoria: Categoria,
        prioridad: Prioridad,
        ubicacionOpcional: String?,
        avisoOpcional: String?,
        notaOpcional: String? = null
    ): ResultadoGuardadoRecordatorio {
        val camposConError = mutableListOf<String>()
        val errorTitulo = validarTitulo(titulo)
        if (errorTitulo != null) {
            camposConError.add("titulo")
        }

        if (camposConError.isNotEmpty()) {
            return ResultadoGuardadoRecordatorio.ErrorValidacion(
                camposInvalidos = camposConError.toList(),
                mensaje = errorTitulo ?: "Revisa los campos obligatorios"
            )
        }

        return runCatching {
            Recordatorio(
                id = idNuevo,
                titulo = titulo.trim(),
                categoria = categoria,
                prioridad = prioridad,
                estado = EstadoRecordatorio.Pendiente,
                ubicacionOpcional = normalizarTextoOpcional(ubicacionOpcional),
                avisoOpcional = normalizarTextoOpcional(avisoOpcional),
                notaOpcional = normalizarTextoOpcional(notaOpcional),
                sincronizadoNube = false
            )
        }.fold(
            onSuccess = { recordatorioValido ->
                ResultadoGuardadoRecordatorio.Exito(recordatorio = recordatorioValido)
            },
            onFailure = { error ->
                ResultadoGuardadoRecordatorio.ErrorValidacion(
                    camposInvalidos = listOf("general"),
                    mensaje = error.message ?: "No fue posible registrar el recordatorio"
                )
            }
        )
    }
}
