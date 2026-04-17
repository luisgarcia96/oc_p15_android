package com.example.eventorias.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Switch
import androidx.compose.material3.TextField
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventorias.R
import com.example.eventorias.auth.AuthUiState
import com.example.eventorias.events.CreateEventEffect
import com.example.eventorias.events.CreateEventViewModel
import com.example.eventorias.events.Event
import com.example.eventorias.events.EventsListEffect
import com.example.eventorias.events.EventsListUiState
import com.example.eventorias.events.EventsListViewModel
import com.example.eventorias.ui.components.EventoriasPrimaryButton
import com.example.eventorias.ui.components.EventoriasSquareIconButton
import com.example.eventorias.ui.theme.EventoriasBackground
import com.example.eventorias.ui.theme.EventoriasOnPrimary
import com.example.eventorias.ui.theme.EventoriasOnSurface
import com.example.eventorias.ui.theme.EventoriasOnSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasPrimary
import com.example.eventorias.ui.theme.EventoriasSurface
import com.example.eventorias.ui.theme.EventoriasSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class HomeTab {
  Events,
  Profile
}

private sealed interface EventsScreenMode {
  data object Home : EventsScreenMode
  data object CreateEvent : EventsScreenMode
  data class EventDetail(val event: Event) : EventsScreenMode
}

@Composable
fun EventsHomeScreen(
  uiState: AuthUiState,
  onSignOut: () -> Unit,
  notificationsEnabled: Boolean,
  onNotificationsToggle: (Boolean) -> Unit,
  onAddEventClick: () -> Unit = {}
) {
  val createEventViewModel: CreateEventViewModel = viewModel()
  val createEventUiState by createEventViewModel.uiState.collectAsState()
  val eventsListViewModel: EventsListViewModel = viewModel()
  val eventsListUiState by eventsListViewModel.uiState.collectAsState()
  var selectedTab by remember { mutableStateOf(HomeTab.Events) }
  var screenMode by remember { mutableStateOf<EventsScreenMode>(EventsScreenMode.Home) }
  val snackbarHostState = remember { SnackbarHostState() }
  val eventSavedMessage = stringResource(R.string.event_save_success)

  LaunchedEffect(createEventViewModel) {
    createEventViewModel.effects.collect { effect ->
      when (effect) {
        CreateEventEffect.EventSaved -> {
          screenMode = EventsScreenMode.Home
          snackbarHostState.showSnackbar(message = eventSavedMessage)
        }
      }
    }
  }

  LaunchedEffect(eventsListViewModel) {
    eventsListViewModel.effects.collect { effect ->
      when (effect) {
        EventsListEffect.EventDeleted -> screenMode = EventsScreenMode.Home
      }
    }
  }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = EventoriasBackground,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    floatingActionButton = {
      if (screenMode is EventsScreenMode.Home && selectedTab == HomeTab.Events) {
        EventoriasSquareIconButton(
          onClick = {
            onAddEventClick()
            screenMode = EventsScreenMode.CreateEvent
          },
          icon = Icons.Outlined.Add,
          contentDescription = stringResource(R.string.add_event)
        )
      }
    },
    bottomBar = {
      if (screenMode is EventsScreenMode.Home) {
        HomeBottomBar(
          selectedTab = selectedTab,
          onSelectTab = { selectedTab = it }
        )
      }
    }
  ) { innerPadding ->
    Box(
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
    ) {
      when (val mode = screenMode) {
        is EventsScreenMode.CreateEvent -> CreateEventScreen(
          uiState = createEventUiState,
          snackbarHostState = snackbarHostState,
          onTitleChanged = createEventViewModel::updateTitle,
          onDescriptionChanged = createEventViewModel::updateDescription,
          onDateChanged = createEventViewModel::updateDate,
          onTimeChanged = createEventViewModel::updateTime,
          onAddressChanged = createEventViewModel::updateAddress,
          onImageSelected = createEventViewModel::setSelectedImage,
          onBack = { screenMode = EventsScreenMode.Home },
          onValidate = createEventViewModel::saveEvent
        )
        is EventsScreenMode.EventDetail -> EventDetailScreen(
          event = mode.event,
          isDeleting = eventsListUiState.isDeleting,
          onBack = { screenMode = EventsScreenMode.Home },
          onDelete = { eventsListViewModel.deleteEvent(mode.event) }
        )
        is EventsScreenMode.Home -> Column(
          modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
        ) {
          HomeHeader(
            selectedTab = selectedTab,
            photoUrl = uiState.currentUser?.photoUrl?.toString()
          )
          when (selectedTab) {
            HomeTab.Events -> EventsTabContent(
              uiState = eventsListUiState,
              onEventClick = { event -> screenMode = EventsScreenMode.EventDetail(event) }
            )
            HomeTab.Profile -> ProfileTabContent(
              uiState = uiState,
              onSignOut = onSignOut,
              notificationsEnabled = notificationsEnabled,
              onNotificationsToggle = onNotificationsToggle
            )
          }
        }
      }
    }
  }
}

@Composable
private fun EventsTabContent(uiState: EventsListUiState, onEventClick: (Event) -> Unit) {
  Spacer(modifier = Modifier.height(16.dp))

  if (!uiState.isLoading && uiState.events.isEmpty()) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = EventoriasSurface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .background(EventoriasSurfaceMuted, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = EventoriasOnSurface,
            modifier = Modifier.size(34.dp)
          )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = stringResource(R.string.no_events_title),
          style = MaterialTheme.typography.titleLarge,
          color = EventoriasOnSurface,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = stringResource(R.string.no_events_message),
          style = MaterialTheme.typography.bodyLarge,
          color = EventoriasOnSurfaceMuted,
          textAlign = TextAlign.Center
        )
      }
    }
  } else {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(uiState.events, key = { it.id }) { event ->
        EventCard(event = event, onClick = { onEventClick(event) })
      }
    }
  }
}

private val EVENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())

@Composable
private fun EventCard(event: Event, onClick: () -> Unit) {
  val formattedDate = remember(event.eventAt) {
    val instant = Instant.ofEpochSecond(event.eventAt.seconds)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    EVENT_DATE_FORMATTER.format(localDate)
  }
  val cardLabel = "${event.title}, $formattedDate"
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(76.dp)
      .clickable(onClickLabel = event.title, role = Role.Button, onClick = onClick)
      .semantics(mergeDescendants = true) {
        contentDescription = cardLabel
      },
    shape = RoundedCornerShape(12.dp),
    color = EventoriasSurface
  ) {
    Row(modifier = Modifier.fillMaxSize()) {
      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
          .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (event.ownerPhotoUrl != null) {
          AsyncImage(
            model = event.ownerPhotoUrl,
            contentDescription = null,
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(EventoriasSurfaceMuted, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.PersonOutline,
              contentDescription = null,
              tint = EventoriasOnSurface,
              modifier = Modifier.size(24.dp)
            )
          }
        }
        Column {
          Text(
            text = event.title,
            style = MaterialTheme.typography.titleSmall,
            color = EventoriasOnSurface,
            maxLines = 1
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = EventoriasOnSurfaceMuted
          )
        }
      }
      AsyncImage(
        model = event.imageUrl,
        contentDescription = null,
        modifier = Modifier
          .width(100.dp)
          .fillMaxHeight(),
        contentScale = ContentScale.Crop
      )
    }
  }
}

@Composable
private fun ProfileTabContent(
  uiState: AuthUiState,
  onSignOut: () -> Unit,
  notificationsEnabled: Boolean,
  onNotificationsToggle: (Boolean) -> Unit
) {
  val displayName = uiState.currentUser?.displayName.orEmpty()
  val email = uiState.currentUser?.email.orEmpty()

  Spacer(modifier = Modifier.height(24.dp))

  Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
    ProfileInfoField(label = stringResource(R.string.profile_field_name), value = displayName, enabled = false)
    ProfileInfoField(label = stringResource(R.string.profile_field_email), value = email, enabled = false)

    val notificationsLabel = stringResource(R.string.profile_notifications)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(4.dp),
      color = EventoriasSurface
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .semantics(mergeDescendants = true) {
            contentDescription = notificationsLabel
          },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Switch(
          checked = notificationsEnabled,
          onCheckedChange = onNotificationsToggle,
          colors = SwitchDefaults.colors(
            checkedTrackColor = EventoriasPrimary,
            checkedThumbColor = Color.White
          )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          text = notificationsLabel,
          style = MaterialTheme.typography.bodyLarge,
          color = EventoriasOnSurface
        )
      }
    }
  }
}

@Composable
private fun ProfileInfoField(label: String, value: String, enabled: Boolean = true) {
  TextField(
    value = value,
    onValueChange = {},
    label = { Text(label) },
    enabled = enabled,
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    colors = TextFieldDefaults.colors(
      focusedContainerColor = EventoriasSurface,
      unfocusedContainerColor = EventoriasSurface,
      disabledContainerColor = EventoriasSurface,
      focusedTextColor = EventoriasOnSurface,
      unfocusedTextColor = EventoriasOnSurface,
      disabledTextColor = EventoriasOnSurface.copy(alpha = 0.5f),
      focusedLabelColor = EventoriasOnSurfaceMuted,
      unfocusedLabelColor = EventoriasOnSurfaceMuted,
      disabledLabelColor = EventoriasOnSurfaceMuted.copy(alpha = 0.5f),
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      disabledIndicatorColor = Color.Transparent,
      cursorColor = EventoriasPrimary
    ),
    textStyle = MaterialTheme.typography.bodyLarge,
    shape = RoundedCornerShape(4.dp)
  )
}

@Composable
private fun HomeHeader(
  selectedTab: HomeTab,
  photoUrl: String? = null
) {
  Spacer(modifier = Modifier.height(18.dp))
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = if (selectedTab == HomeTab.Events) {
        stringResource(R.string.event_list_title)
      } else {
        stringResource(R.string.profile_title)
      },
      style = MaterialTheme.typography.headlineMedium,
      color = EventoriasOnSurface
    )
    Box(
      modifier = Modifier.size(width = 96.dp, height = 48.dp),
      contentAlignment = Alignment.CenterEnd
    ) {
      when (selectedTab) {
        HomeTab.Events -> Unit
        HomeTab.Profile -> if (photoUrl != null) {
          AsyncImage(
            model = photoUrl,
            contentDescription = stringResource(R.string.profile_photo),
            modifier = Modifier.size(44.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = Modifier.size(44.dp).background(EventoriasSurfaceMuted, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.AccountCircle,
              contentDescription = stringResource(R.string.profile_placeholder_user),
              tint = EventoriasOnSurface,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HomeBottomBar(
  selectedTab: HomeTab,
  onSelectTab: (HomeTab) -> Unit
) {
  Surface(
    modifier = Modifier.navigationBarsPadding(),
    color = EventoriasBackground,
    tonalElevation = 0.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 28.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      BottomNavItem(
        selected = selectedTab == HomeTab.Events,
        onClick = { onSelectTab(HomeTab.Events) },
        icon = Icons.Outlined.CalendarMonth,
        label = stringResource(R.string.events_tab)
      )
      Spacer(modifier = Modifier.size(22.dp))
      BottomNavItem(
        selected = selectedTab == HomeTab.Profile,
        onClick = { onSelectTab(HomeTab.Profile) },
        icon = Icons.Outlined.PersonOutline,
        label = stringResource(R.string.profile_tab)
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  selected: Boolean,
  onClick: () -> Unit,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String
) {
  Column(
    modifier = Modifier
      .clickable(onClickLabel = label, role = Role.Tab, onClick = onClick)
      .semantics(mergeDescendants = true) {
        this.selected = selected
        contentDescription = label
      },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Surface(
      shape = RoundedCornerShape(32.dp),
      color = if (selected) EventoriasSurfaceMuted else Color.Transparent
    ) {
      Box(
        modifier = Modifier
          .width(64.dp)
          .height(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = EventoriasOnSurface
        )
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
      ),
      color = EventoriasOnSurface
    )
  }
}

@Preview(name = "Events Home", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun EventsHomeScreenPreview() {
  EventoriasTheme {
    EventsHomeScreen(
      uiState = AuthUiState(),
      onSignOut = {},
      notificationsEnabled = true,
      onNotificationsToggle = {}
    )
  }
}
