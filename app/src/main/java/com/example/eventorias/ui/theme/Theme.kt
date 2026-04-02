package com.example.eventorias.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = EventoriasPrimary,
  onPrimary = EventoriasOnPrimary,
  secondary = EventoriasSurfaceMuted,
  background = EventoriasBackground,
  onBackground = EventoriasOnSurface,
  surface = EventoriasSurface,
  onSurface = EventoriasOnSurface,
  surfaceVariant = EventoriasSurfaceMuted,
  onSurfaceVariant = EventoriasOnSurfaceMuted,
  outline = EventoriasDivider
)

private val LightColorScheme = lightColorScheme(
  primary = EventoriasPrimary,
  onPrimary = EventoriasOnPrimary,
  secondary = EventoriasSurfaceMuted,
  background = EventoriasBackground,
  onBackground = EventoriasOnSurface,
  surface = EventoriasSurface,
  onSurface = EventoriasOnSurface,
  surfaceVariant = EventoriasSurfaceMuted,
  onSurfaceVariant = EventoriasOnSurfaceMuted,
  outline = EventoriasDivider
)

@Composable
fun EventoriasTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
