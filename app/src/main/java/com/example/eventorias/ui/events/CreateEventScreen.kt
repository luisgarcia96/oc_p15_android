package com.example.eventorias.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.ui.components.EventoriasLightSquareIconButton
import com.example.eventorias.ui.components.EventoriasPrimaryButton
import com.example.eventorias.ui.components.EventoriasSquareIconButton
import com.example.eventorias.ui.theme.EventoriasBackground
import com.example.eventorias.ui.theme.EventoriasDivider
import com.example.eventorias.ui.theme.EventoriasOnSurface
import com.example.eventorias.ui.theme.EventoriasOnSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasPrimary
import com.example.eventorias.ui.theme.EventoriasSurface
import com.example.eventorias.ui.theme.EventoriasTheme
import androidx.compose.ui.res.stringResource

@Composable
fun CreateEventScreen(
  onBack: () -> Unit,
  onValidate: () -> Unit = {}
) {
  var title by remember { mutableStateOf("New event") }
  var description by remember { mutableStateOf("Tap here to enter your description") }
  var date by remember { mutableStateOf("MM/DD/YYYY") }
  var time by remember { mutableStateOf("HH : MM") }
  var address by remember { mutableStateOf("Enter full address") }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = EventoriasBackground
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              EventoriasBackground,
              EventoriasBackground,
              Color(0xFF18141A)
            )
          )
        )
        .padding(innerPadding)
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.height(18.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.back),
            tint = EventoriasOnSurface
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = stringResource(R.string.create_event_title),
          style = MaterialTheme.typography.headlineMedium,
          color = EventoriasOnSurface
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      EventInputField(
        label = stringResource(R.string.event_title_label),
        value = title,
        onValueChange = { title = it }
      )

      Spacer(modifier = Modifier.height(18.dp))

      EventInputField(
        label = stringResource(R.string.event_description_label),
        value = description,
        onValueChange = { description = it },
        minLines = 3
      )

      Spacer(modifier = Modifier.height(18.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        EventInputField(
          label = stringResource(R.string.event_date_label),
          value = date,
          onValueChange = { date = it },
          modifier = Modifier.weight(1f),
          keyboardType = KeyboardType.Number
        )
        EventInputField(
          label = stringResource(R.string.event_time_label),
          value = time,
          onValueChange = { time = it },
          modifier = Modifier.weight(1f),
          keyboardType = KeyboardType.Number
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      EventInputField(
        label = stringResource(R.string.event_address_label),
        value = address,
        onValueChange = { address = it }
      )

      Spacer(modifier = Modifier.height(56.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        EventoriasLightSquareIconButton(
          onClick = {},
          icon = Icons.Outlined.CameraAlt,
          contentDescription = stringResource(R.string.add_event_photo)
        )
        Spacer(modifier = Modifier.width(22.dp))
        EventoriasSquareIconButton(
          onClick = {},
          icon = Icons.Outlined.AttachFile,
          contentDescription = stringResource(R.string.add_event_attachment)
        )
      }

      Spacer(modifier = Modifier.height(220.dp))

      EventoriasPrimaryButton(
        text = stringResource(R.string.validate_event),
        onClick = onValidate,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(18.dp))
    }
  }
}

@Composable
private fun EventInputField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  minLines: Int = 1,
  keyboardType: KeyboardType = KeyboardType.Text
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    singleLine = minLines == 1,
    minLines = minLines,
    modifier = modifier.fillMaxWidth(),
    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    colors = TextFieldDefaults.colors(
      focusedContainerColor = EventoriasSurface,
      unfocusedContainerColor = EventoriasSurface,
      disabledContainerColor = EventoriasSurface,
      focusedTextColor = EventoriasOnSurface,
      unfocusedTextColor = EventoriasOnSurface,
      focusedLabelColor = EventoriasOnSurfaceMuted,
      unfocusedLabelColor = EventoriasOnSurfaceMuted,
      focusedIndicatorColor = EventoriasDivider,
      unfocusedIndicatorColor = EventoriasDivider,
      cursorColor = EventoriasPrimary
    ),
    textStyle = MaterialTheme.typography.titleMedium,
    shape = RoundedCornerShape(4.dp)
  )
}

@Preview(name = "Create Event", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun CreateEventScreenPreview() {
  EventoriasTheme {
    CreateEventScreen(onBack = {})
  }
}
