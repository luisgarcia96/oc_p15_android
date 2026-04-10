package com.example.eventorias.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventsListUiState(
  val events: List<Event> = emptyList(),
  val isLoading: Boolean = true,
  val isDeleting: Boolean = false
)

sealed interface EventsListEffect {
  data object EventDeleted : EventsListEffect
}

class EventsListViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = EventsRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(EventsListUiState())
  val uiState: StateFlow<EventsListUiState> = _uiState.asStateFlow()

  private val _effects = MutableSharedFlow<EventsListEffect>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )
  val effects: SharedFlow<EventsListEffect> = _effects.asSharedFlow()

  init {
    viewModelScope.launch {
      repository.getEvents()
        .catch { _uiState.update { it.copy(isLoading = false) } }
        .collect { events ->
          _uiState.update { EventsListUiState(events = events, isLoading = false) }
        }
    }
  }

  fun deleteEvent(event: Event) {
    viewModelScope.launch {
      _uiState.update { it.copy(isDeleting = true) }
      runCatching {
        repository.deleteEvent(event.id, event.imagePath)
      }.onSuccess {
        _uiState.update { it.copy(isDeleting = false) }
        _effects.tryEmit(EventsListEffect.EventDeleted)
      }.onFailure {
        _uiState.update { it.copy(isDeleting = false) }
      }
    }
  }
}
