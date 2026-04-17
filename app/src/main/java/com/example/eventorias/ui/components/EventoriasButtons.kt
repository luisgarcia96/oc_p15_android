package com.example.eventorias.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.eventorias.ui.theme.EventoriasCard
import com.example.eventorias.ui.theme.EventoriasCardText
import com.example.eventorias.ui.theme.EventoriasOnPrimary
import com.example.eventorias.ui.theme.EventoriasPrimary

private val NormalButtonHeight = 58.dp
private val SquareButtonSize = 56.dp
private val SquareButtonRadius = 16.dp

@Composable
fun EventoriasPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  loading: Boolean = false
) {
  val interactive = enabled && !loading
  Surface(
    modifier = modifier
      .clickable(
        enabled = interactive,
        onClickLabel = text,
        role = Role.Button,
        onClick = onClick
      )
      .semantics(mergeDescendants = true) {
        if (!interactive) disabled()
      },
    shape = RoundedCornerShape(4.dp),
    color = if (enabled) EventoriasPrimary else EventoriasPrimary.copy(alpha = 0.45f)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(NormalButtonHeight),
      contentAlignment = Alignment.Center
    ) {
      if (loading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          strokeWidth = 2.dp,
          color = EventoriasOnPrimary
        )
      } else {
        Text(
          text = text,
          style = MaterialTheme.typography.labelLarge,
          color = EventoriasOnPrimary
        )
      }
    }
  }
}

@Composable
fun EventoriasSquareIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  containerColor: Color = EventoriasPrimary,
  contentColor: Color = EventoriasOnPrimary,
  icon: ImageVector? = null,
  contentDescription: String? = null,
  content: (@Composable BoxScope.() -> Unit)? = null
) {
  Surface(
    modifier = modifier
      .clickable(
        enabled = enabled,
        onClickLabel = contentDescription,
        role = Role.Button,
        onClick = onClick
      )
      .semantics(mergeDescendants = true) {
        if (!enabled) disabled()
      },
    shape = RoundedCornerShape(SquareButtonRadius),
    color = if (enabled) containerColor else containerColor.copy(alpha = 0.45f)
  ) {
    Box(
      modifier = Modifier.size(SquareButtonSize),
      contentAlignment = Alignment.Center
    ) {
      when {
        content != null -> content()
        icon != null -> Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          tint = contentColor
        )
      }
    }
  }
}

@Composable
fun EventoriasLightSquareIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: ImageVector,
  contentDescription: String? = null
) {
  EventoriasSquareIconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    containerColor = EventoriasCard,
    contentColor = EventoriasCardText,
    icon = icon,
    contentDescription = contentDescription
  )
}
