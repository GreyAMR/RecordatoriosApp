package com.uni.recordatorios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.uni.recordatorios.R
import com.uni.recordatorios.domain.Categoria
import com.uni.recordatorios.domain.EstadoRecordatorio
import com.uni.recordatorios.domain.FiltroEstado
import com.uni.recordatorios.domain.FiltroPrioridad
import com.uni.recordatorios.domain.Prioridad
import com.uni.recordatorios.domain.Recordatorio
import com.uni.recordatorios.domain.RepositorioRecordatoriosSimulados
import com.uni.recordatorios.domain.ResultadoGuardadoRecordatorio
import com.uni.recordatorios.domain.alternarEstadoCompletado
import com.uni.recordatorios.domain.estaCompletado
import com.uni.recordatorios.domain.obtenerAvisoVisible
import com.uni.recordatorios.domain.obtenerUbicacionVisible
import com.uni.recordatorios.domain.textoDescriptivo
import com.uni.recordatorios.ui.theme.RecordatoriosAppTheme
import com.uni.recordatorios.validation.ValidadorRecordatorio

@Composable
fun RecordatoriosScreen(modifier: Modifier = Modifier) {
    var listaRecordatorios by remember {
        mutableStateOf(RepositorioRecordatoriosSimulados.listaInicialRecordatorios)
    }

    var nuevoTitulo by rememberSaveable { mutableStateOf("") }
    var nuevaUbicacion by rememberSaveable { mutableStateOf("") }
    var categoriaIdFormulario by rememberSaveable {
        mutableStateOf(RepositorioRecordatoriosSimulados.categoriaTrabajoRegional.id)
    }
    var codigoPrioridadFormulario by rememberSaveable { mutableStateOf("ALTA") }
    var mensajeConfirmacion by rememberSaveable { mutableStateOf<String?>(null) }
    var mostrarFormulario by rememberSaveable { mutableStateOf(true) }

    var filtroEstadoActual by rememberSaveable { mutableStateOf(FiltroEstado.TODOS) }
    var filtroPrioridadActual by rememberSaveable { mutableStateOf(FiltroPrioridad.TODAS) }
    var categoriaFiltroId by rememberSaveable { mutableStateOf<String?>(null) }

    val mensajeErrorTitulo = ValidadorRecordatorio.validarTitulo(nuevoTitulo)
    val tituloEsValido = ValidadorRecordatorio.esTituloValido(nuevoTitulo)

    val categoriasSet = RepositorioRecordatoriosSimulados.categoriasDisponiblesSet

    val recordatoriosFiltrados = RepositorioRecordatoriosSimulados.filtrarRecordatorios(
        recordatorios = listaRecordatorios,
        filtroEstado = filtroEstadoActual,
        filtroPrioridad = filtroPrioridadActual,
        categoriaIdSeleccionada = categoriaFiltroId
    )

    val totalPendientes = RepositorioRecordatoriosSimulados.obtenerTitulosPendientes(listaRecordatorios).size
    val totalCompletados = listaRecordatorios.count { it.estaCompletado() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                EncabezadoResumenCard(
                    totalRecordatorios = listaRecordatorios.size,
                    pendientes = totalPendientes,
                    completados = totalCompletados,
                    cantidadCategoriasUnicas = categoriasSet.size,
                    mostrarFormulario = mostrarFormulario,
                    alAlternarFormulario = { mostrarFormulario = !mostrarFormulario }
                )
            }

            if (mostrarFormulario) {
                item {
                    FormularioNuevoRecordatorioCard(
                        titulo = nuevoTitulo,
                        alCambiarTitulo = {
                            nuevoTitulo = it
                            mensajeConfirmacion = null
                        },
                        ubicacion = nuevaUbicacion,
                        alCambiarUbicacion = { nuevaUbicacion = it },
                        categoriaIdSeleccionada = categoriaIdFormulario,
                        alSeleccionarCategoriaId = { categoriaIdFormulario = it },
                        codigoPrioridad = codigoPrioridadFormulario,
                        alSeleccionarPrioridad = { codigoPrioridadFormulario = it },
                        mensajeErrorTitulo = mensajeErrorTitulo,
                        tituloValido = tituloEsValido,
                        mensajeConfirmacion = mensajeConfirmacion,
                        alGuardar = {
                            val categoriaElegida = categoriasSet.firstOrNull {
                                it.id == categoriaIdFormulario
                            } ?: RepositorioRecordatoriosSimulados.categoriaTrabajoRegional

                            val resultado = ValidadorRecordatorio.validarYCrearRecordatorio(
                                idNuevo = (listaRecordatorios.maxOfOrNull { it.id } ?: 100L) + 1L,
                                titulo = nuevoTitulo,
                                categoria = categoriaElegida,
                                prioridad = Prioridad.desdeCodigo(codigoPrioridadFormulario),
                                ubicacionOpcional = nuevaUbicacion,
                                avisoOpcional = "Programado hoy"
                            )

                            when (resultado) {
                                is ResultadoGuardadoRecordatorio.Exito -> {
                                    listaRecordatorios = listOf(resultado.recordatorio) + listaRecordatorios
                                    nuevoTitulo = ""
                                    nuevaUbicacion = ""
                                    mensajeConfirmacion =
                                        "Recordatorio #${resultado.recordatorio.id} registrado correctamente."
                                }
                                is ResultadoGuardadoRecordatorio.ErrorValidacion -> {
                                    mensajeConfirmacion = resultado.mensaje
                                }
                                is ResultadoGuardadoRecordatorio.Guardando -> Unit
                                is ResultadoGuardadoRecordatorio.SinCambios -> Unit
                            }
                        }
                    )
                }
            }

            item {
                SeccionCategoriasYFiltrosCard(
                    categoriasUnicas = categoriasSet.toList(),
                    categoriaSeleccionadaId = categoriaFiltroId,
                    alSeleccionarCategoria = { categoriaFiltroId = it },
                    filtroEstadoActual = filtroEstadoActual,
                    alCambiarFiltroEstado = { filtroEstadoActual = it },
                    filtroPrioridadActual = filtroPrioridadActual,
                    alCambiarFiltroPrioridad = { filtroPrioridadActual = it }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.lista_recordatorios),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Mostrando ${recordatoriosFiltrados.size} de ${listaRecordatorios.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (recordatoriosFiltrados.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.sin_resultados),
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(
                    items = recordatoriosFiltrados,
                    key = { it.id }
                ) { recordatorio ->
                    TarjetaRecordatorioItem(
                        recordatorio = recordatorio,
                        alAlternarCompletado = {
                            listaRecordatorios = listaRecordatorios.map { actual ->
                                if (actual.id == recordatorio.id) {
                                    actual.alternarEstadoCompletado()
                                } else {
                                    actual
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EncabezadoResumenCard(
    totalRecordatorios: Int,
    pendientes: Int,
    completados: Int,
    cantidadCategoriasUnicas: Int,
    mostrarFormulario: Boolean,
    alAlternarFormulario: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.subtitulo_app),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                OutlinedButton(
                    onClick = alAlternarFormulario,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (mostrarFormulario) "Ocultar" else "Nuevo",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndicadorMetricaResumen(
                    etiqueta = "Total",
                    valor = totalRecordatorios.toString(),
                    modifier = Modifier.weight(1f)
                )
                IndicadorMetricaResumen(
                    etiqueta = "Activos",
                    valor = pendientes.toString(),
                    modifier = Modifier.weight(1f)
                )
                IndicadorMetricaResumen(
                    etiqueta = "Hechos",
                    valor = completados.toString(),
                    modifier = Modifier.weight(1f)
                )
                IndicadorMetricaResumen(
                    etiqueta = "Categorias",
                    valor = cantidadCategoriasUnicas.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun IndicadorMetricaResumen(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorDesplegableCategoria(
    etiquetaCampo: String,
    textoSeleccionado: String,
    opciones: List<Pair<String?, String>>,
    alSeleccionarOpcion: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = textoSeleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = etiquetaCampo) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            singleLine = true,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            opciones.forEach { (idOpcion, nombreOpcion) ->
                DropdownMenuItem(
                    text = { Text(text = nombreOpcion) },
                    onClick = {
                        alSeleccionarOpcion(idOpcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun BotonOpcionUniforme(
    texto: String,
    seleccionado: Boolean,
    alHacerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorFondo = if (seleccionado) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val colorTexto = if (seleccionado) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val colorBorde = if (seleccionado) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .border(width = 1.dp, color = colorBorde, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = alHacerClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FormularioNuevoRecordatorioCard(
    titulo: String,
    alCambiarTitulo: (String) -> Unit,
    ubicacion: String,
    alCambiarUbicacion: (String) -> Unit,
    categoriaIdSeleccionada: String,
    alSeleccionarCategoriaId: (String) -> Unit,
    codigoPrioridad: String,
    alSeleccionarPrioridad: (String) -> Unit,
    mensajeErrorTitulo: String?,
    tituloValido: Boolean,
    mensajeConfirmacion: String?,
    alGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categorias = RepositorioRecordatoriosSimulados.categoriasDisponiblesSet.toList()
    val categoriaActual = categorias.firstOrNull { it.id == categoriaIdSeleccionada }
        ?: RepositorioRecordatoriosSimulados.categoriaTrabajoRegional

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.nuevo_recordatorio),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = alCambiarTitulo,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.campo_titulo)) },
                isError = titulo.isNotEmpty() && !tituloValido,
                supportingText = {
                    if (titulo.isNotEmpty() && mensajeErrorTitulo != null) {
                        Text(text = mensajeErrorTitulo)
                    } else {
                        Text(text = "Minimo 4 caracteres.")
                    }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = ubicacion,
                onValueChange = alCambiarUbicacion,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.campo_ubicacion)) },
                singleLine = true
            )

            SelectorDesplegableCategoria(
                etiquetaCampo = "Categoria",
                textoSeleccionado = categoriaActual.nombre,
                opciones = categorias.map { it.id to it.nombre },
                alSeleccionarOpcion = { idElegido ->
                    if (idElegido != null) {
                        alSeleccionarCategoriaId(idElegido)
                    }
                }
            )

            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALTA" to "Alta", "MEDIA" to "Media", "BAJA" to "Baja").forEach { (cod, texto) ->
                    BotonOpcionUniforme(
                        texto = texto,
                        seleccionado = codigoPrioridad == cod,
                        alHacerClick = { alSeleccionarPrioridad(cod) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = alGuardar,
                enabled = tituloValido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.guardar_recordatorio))
            }

            mensajeConfirmacion?.let { confirmacion ->
                Text(
                    text = confirmacion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SeccionCategoriasYFiltrosCard(
    categoriasUnicas: List<Categoria>,
    categoriaSeleccionadaId: String?,
    alSeleccionarCategoria: (String?) -> Unit,
    filtroEstadoActual: FiltroEstado,
    alCambiarFiltroEstado: (FiltroEstado) -> Unit,
    filtroPrioridadActual: FiltroPrioridad,
    alCambiarFiltroPrioridad: (FiltroPrioridad) -> Unit,
    modifier: Modifier = Modifier
) {
    val opcionesFiltroCategoria = listOf<Pair<String?, String>>(
        null to "Todas las categorias"
    ) + categoriasUnicas.map { it.id to it.nombre }

    val textoCategoriaSeleccionada = opcionesFiltroCategoria
        .firstOrNull { it.first == categoriaSeleccionadaId }
        ?.second ?: "Todas las categorias"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.seccion_categorias_set),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            SelectorDesplegableCategoria(
                etiquetaCampo = "Filtrar por categoria",
                textoSeleccionado = textoCategoriaSeleccionada,
                opciones = opcionesFiltroCategoria,
                alSeleccionarOpcion = alSeleccionarCategoria
            )

            Text(
                text = "Estado",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FiltroEstado.entries.forEach { filtro ->
                    BotonOpcionUniforme(
                        texto = filtro.etiqueta,
                        seleccionado = filtroEstadoActual == filtro,
                        alHacerClick = { alCambiarFiltroEstado(filtro) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FiltroPrioridad.entries.forEach { filtro ->
                    BotonOpcionUniforme(
                        texto = filtro.etiqueta,
                        seleccionado = filtroPrioridadActual == filtro,
                        alHacerClick = { alCambiarFiltroPrioridad(filtro) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaRecordatorioItem(
    recordatorio: Recordatorio,
    alAlternarCompletado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estiloCategoria = RepositorioRecordatoriosSimulados.obtenerEstiloCategoria(
        recordatorio.categoria.id
    )

    val (colorPrioridad, fondoPrioridad) = when (recordatorio.prioridad) {
        is Prioridad.Alta -> Color(0xFFB71C1C) to Color(0xFFFFEBEE)
        is Prioridad.Media -> Color(0xFFE65100) to Color(0xFFFFF3E0)
        is Prioridad.Baja -> Color(0xFF1B5E20) to Color(0xFFE8F5E9)
    }

    val (colorEstado, fondoEstado) = when (recordatorio.estado) {
        is EstadoRecordatorio.Pendiente -> Color(0xFF455A64) to Color(0xFFECEFF1)
        is EstadoRecordatorio.EnProgreso -> Color(0xFF0277BD) to Color(0xFFE1F5FE)
        is EstadoRecordatorio.Completado -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EtiquetaPastilla(
                    texto = recordatorio.categoria.nombre,
                    colorTexto = Color(estiloCategoria.colorHex),
                    colorFondo = Color(estiloCategoria.colorFondoHex)
                )

                EtiquetaPastilla(
                    texto = recordatorio.prioridad.nombreVisible,
                    colorTexto = colorPrioridad,
                    colorFondo = fondoPrioridad
                )
            }

            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = if (recordatorio.estaCompletado()) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Ubicacion: ${recordatorio.obtenerUbicacionVisible()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Text(
                    text = "Aviso: ${recordatorio.obtenerAvisoVisible()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                recordatorio.notaOpcional?.let { notaPresente ->
                    Text(
                        text = "Nota: $notaPresente",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EtiquetaPastilla(
                    texto = "Estado: ${recordatorio.estado.textoDescriptivo()}",
                    colorTexto = colorEstado,
                    colorFondo = fondoEstado
                )

                OutlinedButton(onClick = alAlternarCompletado) {
                    Text(
                        text = if (recordatorio.estaCompletado()) "Reabrir" else "Completar",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EtiquetaPastilla(
    texto: String,
    colorTexto: Color,
    colorFondo: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = colorFondo,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1050)
@Composable
fun RecordatoriosScreenPreview() {
    RecordatoriosAppTheme {
        RecordatoriosScreen()
    }
}
