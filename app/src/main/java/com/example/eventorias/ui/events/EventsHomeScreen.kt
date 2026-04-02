package com.example.eventorias.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.auth.AuthUiState
import com.example.eventorias.ui.components.EventoriasPrimaryButton
import com.example.eventorias.ui.components.EventoriasSquareIconButton
import com.example.eventorias.ui.theme.EventoriasBackground
import com.example.eventorias.ui.theme.EventoriasDivider
import com.example.eventorias.ui.theme.EventoriasOnPrimary
import com.example.eventorias.ui.theme.EventoriasOnSurface
import com.example.eventorias.ui.theme.EventoriasOnSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasPrimary
import com.example.eventorias.ui.theme.EventoriasSurface
import com.example.eventorias.ui.theme.EventoriasSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasTheme

private enum class HomeTab {
  Events,
  Profile
}

private enum class EventsScreenMode {
  Home,
  CreateEvent
}

@Composable
fun EventsHomeScreen(
  uiState: AuthUiState,
  onSignOut: () -> Unit,
  onAddEventClick: () -> Unit = {}
) {
  var selectedTab by remember { mutableStateOf(HomeTab.Events) }
  var screenMode by remember { mutableStateOf(EventsScreenMode.Home) }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = EventoriasBackground,
    floatingActionButton = {
      if (screenMode == EventsScreenMode.Home && selectedTab == HomeTab.Events) {
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
      if (screenMode == EventsScreenMode.Home) {
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
      if (screenMode == EventsScreenMode.CreateEvent) {
        CreateEventScreen(
          onBack = { screenMode = EventsScreenMode.Home }
        )
      } else {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
        ) {
          HomeHeader(selectedTab = selectedTab)
          when (selectedTab) {
            HomeTab.Events -> EventsTabContent()
            HomeTab.Profile -> ProfileTabContent(
              uiState = uiState,
              onSignOut = onSignOut
            )
          }
        }
      }
    }
  }
}

@Composable
private fun EventsTabContent() {
  Spacer(modifier = Modifier.height(24.dp))

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
}

@Composable
private fun ProfileTabContent(
  uiState: AuthUiState,
  onSignOut: () -> Unit
) {
  val userLabel = uiState.currentUser?.email
    ?: uiState.currentUser?.displayName
    ?: stringResource(R.string.profile_placeholder_user)

  Spacer(modifier = Modifier.height(24.dp))

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    color = EventoriasSurface
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
      Box(
        modifier = Modifier
          .size(68.dp)
          .background(EventoriasSurfaceMuted, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Outlined.AccountCircle,
          contentDescription = null,
          tint = EventoriasOnSurface,
          modifier = Modifier.size(40.dp)
        )
      }
      Spacer(modifier = Modifier.height(18.dp))
      Text(
        text = userLabel,
        style = MaterialTheme.typography.titleLarge,
        color = EventoriasOnSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.profile_subtitle, userLabel),
        style = MaterialTheme.typography.bodyMedium,
        color = EventoriasOnSurfaceMuted
      )
      Spacer(modifier = Modifier.height(24.dp))
      EventoriasPrimaryButton(
        text = stringResource(R.string.sign_out_cta),
        onClick = onSignOut,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun HomeHeader(
  selectedTab: HomeTab
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
      if (selectedTab == HomeTab.Events) {
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
          IconButton(onClick = {}) {
            Icon(
              imageVector = Icons.Outlined.Search,
              contentDescription = stringResource(R.string.search_events),
              tint = EventoriasOnSurface
            )
          }
          IconButton(onClick = {}) {
            Icon(
              imageVector = Icons.Outlined.SwapVert,
              contentDescription = stringResource(R.string.sort_events),
              tint = EventoriasOnSurface
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
    modifier = Modifier.clickable(onClick = onClick),
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
          contentDescription = label,
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
      onSignOut = {}
    )
  }
}
