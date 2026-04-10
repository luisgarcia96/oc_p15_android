package com.example.eventorias.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventsListUiState(
  val events: List<Event> = emptyList(),
  val isLoading: Boolean = true
)

class EventsListViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = EventsRepository(application.applicationContext)

  private val _uiState = MutableStateFlow(EventsListUiState())
  val uiState: StateFlow<EventsListUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      repository.getEvents()
        .catch { _uiState.update { it.copy(isLoading = false) } }
        .collect { events ->
          _uiState.update { EventsListUiState(events = events, isLoading = false) }
        }
    }
  }
}
