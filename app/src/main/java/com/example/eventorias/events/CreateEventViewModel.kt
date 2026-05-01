package com.example.eventorias.events

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventorias.R
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateEventUiState(
  val title: String = "",
  val description: String = "",
  val date: String = "",
  val time: String = "",
  val address: String = "",
  val imageUri: Uri? = null,
  val imageLabel: String? = null,
  val isSaving: Boolean = false,
  val errorMessage: String? = null
)

sealed interface CreateEventEffect {
  data object EventSaved : CreateEventEffect
}

class CreateEventViewModel(
  application: Application
) : AndroidViewModel(application) {
  private val appContext = application.applicationContext
  private val repository = EventsRepository(appContext)
  private val reminderScheduler = EventReminderScheduler(appContext)
  private val auth = FirebaseAuth.getInstance()

  private val mutableUiState = MutableStateFlow(
    CreateEventUiState()
  )
  val uiState: StateFlow<CreateEventUiState> = mutableUiState.asStateFlow()

  private val mutableEffects = MutableSharedFlow<CreateEventEffect>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
  val effects: SharedFlow<CreateEventEffect> = mutableEffects.asSharedFlow()

  fun updateTitle(value: String) {
    mutableUiState.update { it.copy(title = value, errorMessage = null) }
  }

  fun updateDescription(value: String) {
    mutableUiState.update { it.copy(description = value, errorMessage = null) }
  }

  fun updateDate(value: String) {
    mutableUiState.update { it.copy(date = value, errorMessage = null) }
  }

  fun updateTime(value: String) {
    mutableUiState.update { it.copy(time = value, errorMessage = null) }
  }

  fun updateAddress(value: String) {
    mutableUiState.update { it.copy(address = value, errorMessage = null) }
  }

  fun setSelectedImage(uri: Uri) {
    mutableUiState.update {
      it.copy(
        imageUri = uri,
        imageLabel = resolveImageLabel(uri),
        errorMessage = null
      )
    }
  }

  fun saveEvent() {
    val currentState = mutableUiState.value
    val validationError = validate(currentState)
    if (validationError != null) {
      mutableUiState.update { it.copy(errorMessage = validationError) }
      return
    }

    viewModelScope.launch {
      mutableUiState.update { it.copy(isSaving = true, errorMessage = null) }

      runCatching {
        repository.createEvent(
          EventDraft(
            title = currentState.title,
            description = currentState.description,
            date = currentState.date,
            time = currentState.time,
            address = currentState.address,
            imageUri = currentState.imageUri
          )
        )
      }.onSuccess { eventId ->
        reminderScheduler.scheduleReminder(
          eventId = eventId,
          eventTitle = currentState.title.trim(),
          eventAtMillis = parseEventAtMillis(currentState.date, currentState.time)
        )
        resetForm()
        mutableEffects.tryEmit(CreateEventEffect.EventSaved)
      }.onFailure { throwable ->
        mutableUiState.update {
          it.copy(
            isSaving = false,
            errorMessage = throwable.message ?: appContext.getString(R.string.event_save_failed)
          )
        }
      }
    }
  }

  private fun resetForm() {
    mutableUiState.value = CreateEventUiState()
  }

  private fun validate(state: CreateEventUiState): String? {
    if (auth.currentUser == null) {
      return appContext.getString(R.string.event_auth_required)
    }
    if (state.title.trim().isEmpty()) {
      return appContext.getString(R.string.event_title_required)
    }
    if (state.description.trim().isEmpty()) {
      return appContext.getString(R.string.event_description_required)
    }
    if (state.address.trim().isEmpty()) {
      return appContext.getString(R.string.event_address_required)
    }
    if (!isValidDate(state.date)) {
      return appContext.getString(R.string.event_invalid_date)
    }
    if (!isValidTime(state.time)) {
      return appContext.getString(R.string.event_invalid_time)
    }
    if (state.imageUri == null) {
      return appContext.getString(R.string.event_image_required)
    }
    return null
  }

  private fun isValidDate(value: String): Boolean = try {
    LocalDate.parse(value.trim(), DATE_FORMATTER)
    true
  } catch (_: DateTimeParseException) {
    false
  }

  private fun isValidTime(value: String): Boolean = try {
    LocalTime.parse(value.trim().replace(" ", ""), TIME_FORMATTER)
    true
  } catch (_: DateTimeParseException) {
    false
  }

  private fun resolveImageLabel(uri: Uri): String {
    val name = uri.lastPathSegment?.substringAfterLast('/')
    return if (name.isNullOrBlank()) {
      appContext.getString(R.string.event_photo_selected)
    } else {
      appContext.getString(R.string.event_photo_selected_named, name)
    }
  }

  private fun parseEventAtMillis(date: String, time: String): Long {
    val parsedDate = LocalDate.parse(date.trim(), DATE_FORMATTER)
    val parsedTime = LocalTime.parse(time.trim().replace(" ", ""), TIME_FORMATTER)
    return parsedDate
      .atTime(parsedTime)
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli()
  }

  companion object {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/uuuu")
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
  }
}
