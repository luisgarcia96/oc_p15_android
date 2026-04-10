package com.example.eventorias.ui.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.events.CreateEventUiState
import com.example.eventorias.ui.components.EventoriasLightSquareIconButton
import com.example.eventorias.ui.components.EventoriasPrimaryButton
import com.example.eventorias.ui.components.EventoriasSquareIconButton
import com.example.eventorias.ui.theme.EventoriasBackground
import com.example.eventorias.ui.theme.EventoriasOnSurface
import com.example.eventorias.ui.theme.EventoriasOnSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasPrimary
import com.example.eventorias.ui.theme.EventoriasSurface
import com.example.eventorias.ui.theme.EventoriasTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun CreateEventScreen(
  uiState: CreateEventUiState,
  snackbarHostState: SnackbarHostState,
  onTitleChanged: (String) -> Unit,
  onDescriptionChanged: (String) -> Unit,
  onDateChanged: (String) -> Unit,
  onTimeChanged: (String) -> Unit,
  onAddressChanged: (String) -> Unit,
  onImageSelected: (Uri) -> Unit,
  onBack: () -> Unit,
  onValidate: () -> Unit = {}
) {
  val context = LocalContext.current
  val focusManager = LocalFocusManager.current
  val activity = remember(context) { context.findActivity() }
  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri ->
    if (uri != null) {
      onImageSelected(uri)
    }
  }
  DisposableEffect(activity) {
    val window = activity?.window
    val previousSoftInputMode = window?.attributes?.softInputMode
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

    onDispose {
      if (window != null && previousSoftInputMode != null) {
        window.setSoftInputMode(previousSoftInputMode)
      }
    }
  }
  val initialDate = remember(uiState.date) { parseInitialDate(uiState.date) }
  val initialTime = remember(uiState.time) { parseInitialTime(uiState.time) }
  val datePickerDialog = remember(context, initialDate) {
    DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        onDateChanged("%02d/%02d/%04d".format(month + 1, dayOfMonth, year))
      },
      initialDate.year,
      initialDate.monthValue - 1,
      initialDate.dayOfMonth
    )
  }
  val timePickerDialog = remember(context, initialTime) {
    TimePickerDialog(
      context,
      { _, hourOfDay, minute ->
        onTimeChanged("%02d:%02d".format(hourOfDay, minute))
      },
      initialTime.hour,
      initialTime.minute,
      true
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            EventoriasBackground,
            EventoriasBackground,
            Color(0xFF18141A)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .weight(1f)
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
        placeholder = stringResource(R.string.event_title_placeholder),
        value = uiState.title,
        onValueChange = onTitleChanged
      )

      Spacer(modifier = Modifier.height(18.dp))

      EventInputField(
        label = stringResource(R.string.event_description_label),
        placeholder = stringResource(R.string.event_description_placeholder),
        value = uiState.description,
        onValueChange = onDescriptionChanged,
        minLines = 3
      )

      Spacer(modifier = Modifier.height(18.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        EventPickerField(
          label = stringResource(R.string.event_date_label),
          value = uiState.date,
          placeholder = stringResource(R.string.event_date_placeholder),
          modifier = Modifier.weight(1f),
          icon = Icons.Outlined.CalendarMonth,
          onClick = { datePickerDialog.show() }
        )
        EventPickerField(
          label = stringResource(R.string.event_time_label),
          value = uiState.time,
          placeholder = stringResource(R.string.event_time_placeholder),
          modifier = Modifier.weight(1f),
          icon = Icons.Outlined.AccessTime,
          onClick = { timePickerDialog.show() }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      EventInputField(
        label = stringResource(R.string.event_address_label),
        placeholder = stringResource(R.string.event_address_placeholder),
        value = uiState.address,
        onValueChange = onAddressChanged
      )

      Spacer(modifier = Modifier.height(56.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        EventoriasLightSquareIconButton(
          onClick = { imagePickerLauncher.launch("image/*") },
          icon = Icons.Outlined.CameraAlt,
          contentDescription = stringResource(R.string.add_event_photo)
        )
        Spacer(modifier = Modifier.width(22.dp))
        EventoriasSquareIconButton(
          onClick = { imagePickerLauncher.launch("image/*") },
          icon = Icons.Outlined.AttachFile,
          contentDescription = stringResource(R.string.add_event_attachment)
        )
      }

      if (uiState.imageLabel != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = uiState.imageLabel,
          style = MaterialTheme.typography.bodyMedium,
          color = EventoriasOnSurfaceMuted,
          modifier = Modifier.fillMaxWidth()
        )
      }

      if (uiState.errorMessage != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = uiState.errorMessage,
          style = MaterialTheme.typography.bodyMedium,
          color = EventoriasPrimary,
          modifier = Modifier.fillMaxWidth()
        )
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, bottom = 18.dp)
    ) {
      EventoriasPrimaryButton(
        text = stringResource(R.string.validate_event),
        onClick = onValidate,
        modifier = Modifier.fillMaxWidth(),
        loading = uiState.isSaving
      )
    }
  }
}

@Composable
private fun EventInputField(
  label: String,
  placeholder: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  minLines: Int = 1
) {
  TextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = {
      Text(
        text = placeholder,
        color = EventoriasOnSurfaceMuted
      )
    },
    singleLine = minLines == 1,
    minLines = minLines,
    modifier = modifier.fillMaxWidth(),
    colors = TextFieldDefaults.colors(
      focusedContainerColor = EventoriasSurface,
      unfocusedContainerColor = EventoriasSurface,
      disabledContainerColor = EventoriasSurface,
      focusedTextColor = EventoriasOnSurface,
      unfocusedTextColor = EventoriasOnSurface,
      focusedLabelColor = EventoriasOnSurfaceMuted,
      unfocusedLabelColor = EventoriasOnSurfaceMuted,
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      disabledIndicatorColor = Color.Transparent,
      cursorColor = EventoriasPrimary
    ),
    textStyle = MaterialTheme.typography.titleMedium,
    shape = RoundedCornerShape(4.dp)
  )
}

@Composable
private fun EventPickerField(
  label: String,
  value: String,
  placeholder: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    color = EventoriasSurface,
    shape = RoundedCornerShape(4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = EventoriasOnSurfaceMuted
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val displayText = value.ifBlank { placeholder }
        val isPlaceholder = value.isBlank()
        Text(
          text = displayText,
          style = if (isPlaceholder) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
          color = if (isPlaceholder) EventoriasOnSurfaceMuted else EventoriasOnSurface,
          modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = EventoriasOnSurfaceMuted
        )
      }
    }
  }
}

private fun parseInitialDate(value: String): LocalDate = try {
  LocalDate.parse(value.trim(), DATE_FORMATTER)
} catch (_: DateTimeParseException) {
  LocalDate.now()
}

private fun parseInitialTime(value: String): LocalTime = try {
  LocalTime.parse(value.trim().replace(" ", ""), TIME_FORMATTER)
} catch (_: DateTimeParseException) {
  LocalTime.now()
}

private tailrec fun Context.findActivity(): android.app.Activity? = when (this) {
  is android.app.Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/uuuu")
private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Preview(name = "Create Event", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun CreateEventScreenPreview() {
  EventoriasTheme {
    CreateEventScreen(
      uiState = CreateEventUiState(
        title = "",
        description = "",
        date = "06/15/2026",
        time = "20:30",
        address = ""
      ),
      snackbarHostState = SnackbarHostState(),
      onTitleChanged = {},
      onDescriptionChanged = {},
      onDateChanged = {},
      onTimeChanged = {},
      onAddressChanged = {},
      onImageSelected = {},
      onBack = {},
      onValidate = {}
    )
  }
}
