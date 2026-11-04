package com.uni.recordatorios.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaColoresOscuro = darkColorScheme(
    primary = AzulPrimarioOscuro,
    secondary = AzulSecundarioOscuro,
    tertiary = VerdeTerciarioOscuro
)

private val EsquemaColoresClaro = lightColorScheme(
    primary = AzulPrimario,
    secondary = AzulSecundario,
    tertiary = VerdeTerciario,
    background = FondoClaro,
    surface = SuperficieClara
)

@Composable
fun RecordatoriosAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaColoresOscuro else EsquemaColoresClaro

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
