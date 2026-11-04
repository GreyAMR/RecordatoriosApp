package com.uni.recordatorios.domain

enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"),
    PENDIENTES("Pendientes"),
    EN_PROGRESO("En curso"),
    COMPLETADOS("Hechos")
}

enum class FiltroPrioridad(val etiqueta: String) {
    TODAS("Todas"),
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}

object RepositorioRecordatoriosSimulados {

    val categoriaTrabajoRegional = Categoria(
        id = "TRABAJO_REGIONAL",
        nombre = "Trabajo Regional",
        descripcion = "Tareas operativas y reportes de sedes regionales"
    )
    val categoriaTrabajoGlobal = Categoria(
        id = "TRABAJO_GLOBAL",
        nombre = "Trabajo Global",
        descripcion = "Coordinacion general y objetivos corporativos globales"
    )
    val categoriaTrabajoEquipo = Categoria(
        id = "TRABAJO_EQUIPO",
        nombre = "Trabajo de Equipo",
        descripcion = "Actividades colaborativas y seguimiento grupal"
    )
    val categoriaTrabajoPersonal = Categoria(
        id = "TRABAJO_PERSONAL",
        nombre = "Trabajo de Personal",
        descripcion = "Gestion de recursos humanos, turnos y capacitaciones"
    )

    val categoriaTrabajo: Categoria = categoriaTrabajoRegional

    val categoriasDisponiblesSet: Set<Categoria> = setOf(
        categoriaTrabajoRegional,
        categoriaTrabajoGlobal,
        categoriaTrabajoEquipo,
        categoriaTrabajoPersonal,
        categoriaTrabajoRegional
    )

    val mapaEtiquetasYColoresPorCategoria: Map<String, EstiloCategoria> = mapOf(
        categoriaTrabajoRegional.id to EstiloCategoria(
            etiqueta = "[REGIONAL]",
            colorHex = 0xFF0D47A1,
            colorFondoHex = 0xFFE3F2FD
        ),
        categoriaTrabajoGlobal.id to EstiloCategoria(
            etiqueta = "[GLOBAL]",
            colorHex = 0xFF4A148C,
            colorFondoHex = 0xFFF3E5F5
        ),
        categoriaTrabajoEquipo.id to EstiloCategoria(
            etiqueta = "[EQUIPO]",
            colorHex = 0xFF1B5E20,
            colorFondoHex = 0xFFE8F5E9
        ),
        categoriaTrabajoPersonal.id to EstiloCategoria(
            etiqueta = "[PERSONAL]",
            colorHex = 0xFFE65100,
            colorFondoHex = 0xFFFFF3E0
        )
    )

    fun obtenerEstiloCategoria(categoriaId: String): EstiloCategoria =
        mapaEtiquetasYColoresPorCategoria[categoriaId] ?: EstiloCategoria(
            etiqueta = "[GENERAL]",
            colorHex = 0xFF37474F,
            colorFondoHex = 0xFFECEFF1
        )

    val listaInicialRecordatorios: List<Recordatorio> = listOf(
        Recordatorio(
            id = 101L,
            titulo = "Enviar relevamiento tecnico de sede norte",
            categoria = categoriaTrabajoRegional,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.EnProgreso(porcentajeAvance = 75),
            ubicacionOpcional = "Oficina Regional Norte - Piso 2",
            avisoOpcional = "Hoy, 14:00 hs",
            notaOpcional = "Adjuntar planilla de control",
            sincronizadoNube = true
        ),
        Recordatorio(
            id = 102L,
            titulo = "Actualizar indicadores del trimestre internacional",
            categoria = categoriaTrabajoGlobal,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOpcional = "Plataforma Central",
            avisoOpcional = "Viernes, 18:00 hs",
            notaOpcional = "Consolidar metricas anuales",
            sincronizadoNube = true
        ),
        Recordatorio(
            id = 103L,
            titulo = "Reunion de sincronizacion semanal de proyecto",
            categoria = categoriaTrabajoEquipo,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOpcional = "Sala de Juntas B",
            avisoOpcional = null,
            notaOpcional = "Revisar bloqueos del sprint",
            sincronizadoNube = false
        ),
        Recordatorio(
            id = 104L,
            titulo = "Validar cronograma de induccion y legajos",
            categoria = categoriaTrabajoPersonal,
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Completado(fechaFinalizacion = "Ayer, 11:15 hs"),
            ubicacionOpcional = "Departamento de Personal",
            avisoOpcional = "11:00 hs",
            notaOpcional = null,
            sincronizadoNube = true
        ),
        Recordatorio(
            id = 105L,
            titulo = "Asignar guardias operativas del proximo mes",
            categoria = categoriaTrabajoPersonal,
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.Pendiente,
            ubicacionOpcional = null,
            avisoOpcional = "Lunes por la manana",
            notaOpcional = "Confirmar disponibilidad con supervisores",
            sincronizadoNube = false
        ),
        Recordatorio(
            id = 106L,
            titulo = "Sincronizar respaldo local de inspectores",
            categoria = categoriaTrabajoRegional,
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Completado(fechaFinalizacion = "Hoy, 09:00 hs"),
            ubicacionOpcional = "Servidor Local - Nodo Regional",
            avisoOpcional = "09:00 hs",
            notaOpcional = "Sincronizacion verificada",
            sincronizadoNube = true
        )
    )

    fun obtenerCategoriasUnicasDeLista(recordatorios: List<Recordatorio>): Set<Categoria> =
        recordatorios.map { it.categoria }.toSet()

    fun filtrarRecordatorios(
        recordatorios: List<Recordatorio>,
        filtroEstado: FiltroEstado = FiltroEstado.TODOS,
        filtroPrioridad: FiltroPrioridad = FiltroPrioridad.TODAS,
        categoriaIdSeleccionada: String? = null
    ): List<Recordatorio> =
        recordatorios
            .filter { recordatorio ->
                when (filtroEstado) {
                    FiltroEstado.TODOS -> true
                    FiltroEstado.PENDIENTES -> recordatorio.estado is EstadoRecordatorio.Pendiente
                    FiltroEstado.EN_PROGRESO -> recordatorio.estado is EstadoRecordatorio.EnProgreso
                    FiltroEstado.COMPLETADOS -> recordatorio.estado is EstadoRecordatorio.Completado
                }
            }
            .filter { recordatorio ->
                when (filtroPrioridad) {
                    FiltroPrioridad.TODAS -> true
                    FiltroPrioridad.ALTA -> recordatorio.prioridad is Prioridad.Alta
                    FiltroPrioridad.MEDIA -> recordatorio.prioridad is Prioridad.Media
                    FiltroPrioridad.BAJA -> recordatorio.prioridad is Prioridad.Baja
                }
            }
            .filter { recordatorio ->
                categoriaIdSeleccionada == null || recordatorio.categoria.id == categoriaIdSeleccionada
            }

    fun obtenerTitulosPendientes(recordatorios: List<Recordatorio>): List<String> =
        recordatorios
            .filter { !it.estaCompletado() }
            .map { it.titulo.trim() }
            .filter { it.isNotEmpty() }

    fun obtenerRecordatorioSeguroEnPosicion(
        recordatorios: List<Recordatorio>,
        indice: Int
    ): Recordatorio? = recordatorios.getOrNull(indice)
}
