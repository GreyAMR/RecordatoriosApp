package com.uni.recordatorios

import com.uni.recordatorios.domain.EstadoRecordatorio
import com.uni.recordatorios.domain.FiltroEstado
import com.uni.recordatorios.domain.FiltroPrioridad
import com.uni.recordatorios.domain.Prioridad
import com.uni.recordatorios.domain.Recordatorio
import com.uni.recordatorios.domain.RepositorioRecordatoriosSimulados
import com.uni.recordatorios.domain.ResultadoGuardadoRecordatorio
import com.uni.recordatorios.domain.alternarEstadoCompletado
import com.uni.recordatorios.domain.obtenerUbicacionVisible
import com.uni.recordatorios.validation.ValidadorRecordatorio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordatoriosUnitTest {

    @Test
    fun setCategorias_eliminaCategoriasRepetidas() {
        val categorias = RepositorioRecordatoriosSimulados.categoriasDisponiblesSet
        assertEquals(4, categorias.size)
    }

    @Test
    fun mapEstilosCategoria_asociaCadaCategoriaConEtiquetaYColor() {
        val estiloTrabajo = RepositorioRecordatoriosSimulados.obtenerEstiloCategoria("TRABAJO_REGIONAL")
        assertTrue(estiloTrabajo.etiqueta.contains("REGIONAL"))
        assertEquals(0xFF0D47A1, estiloTrabajo.colorHex)
    }

    @Test
    fun filtroPorEstadoYPrioridad_retornaElementosCorrectos() {
        val todos = RepositorioRecordatoriosSimulados.listaInicialRecordatorios
        val altaPrioridad = RepositorioRecordatoriosSimulados.filtrarRecordatorios(
            recordatorios = todos,
            filtroEstado = FiltroEstado.TODOS,
            filtroPrioridad = FiltroPrioridad.ALTA
        )
        assertTrue(altaPrioridad.isNotEmpty())
        assertTrue(altaPrioridad.all { it.prioridad is Prioridad.Alta })
    }

    @Test
    fun validacionTitulo_compruebaCasosVaciosCortosYValidos() {
        assertNotNull(ValidadorRecordatorio.validarTitulo(""))
        assertNotNull(ValidadorRecordatorio.validarTitulo("   "))
        assertNotNull(ValidadorRecordatorio.validarTitulo("abc"))
        assertNull(ValidadorRecordatorio.validarTitulo("Revision general"))
    }

    @Test
    fun inmutabilidadConCopy_noAlteraInstanciaOriginal() {
        val original = Recordatorio(
            id = 1L,
            titulo = "Tarea de prueba",
            categoria = RepositorioRecordatoriosSimulados.categoriaTrabajoRegional,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOpcional = null
        )
        val actualizado = original.alternarEstadoCompletado("Hoy")

        assertTrue(original.estado is EstadoRecordatorio.Pendiente)
        assertTrue(actualizado.estado is EstadoRecordatorio.Completado)
        assertEquals("Sin ubicacion asignada", original.obtenerUbicacionVisible())
    }

    @Test
    fun validarYCrearRecordatorio_retornaResultadoGuardadoExitoso() {
        val resultado = ValidadorRecordatorio.validarYCrearRecordatorio(
            idNuevo = 999L,
            titulo = " calibrar sensores ",
            categoria = RepositorioRecordatoriosSimulados.categoriaTrabajoRegional,
            prioridad = Prioridad.Alta,
            ubicacionOpcional = "   ",
            avisoOpcional = "10:00 hs"
        )
        assertTrue(resultado is ResultadoGuardadoRecordatorio.Exito)
        val creado = (resultado as ResultadoGuardadoRecordatorio.Exito).recordatorio
        assertEquals("calibrar sensores", creado.titulo)
        assertNull(creado.ubicacionOpcional)
        assertFalse(creado.sincronizadoNube)
    }
}
