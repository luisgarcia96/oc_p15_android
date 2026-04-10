package com.example.eventorias.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.eventorias.R
import com.example.eventorias.events.Event
import com.example.eventorias.ui.components.EventoriasPrimaryButton
import com.example.eventorias.ui.theme.EventoriasBackground
import com.example.eventorias.ui.theme.EventoriasOnSurface
import com.example.eventorias.ui.theme.EventoriasOnSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasPrimary
import com.example.eventorias.ui.theme.EventoriasSurface
import com.example.eventorias.ui.theme.EventoriasSurfaceMuted
import android.location.Geocoder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
private val TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

@Composable
fun EventDetailScreen(
  event: Event,
  isDeleting: Boolean,
  onBack: () -> Unit,
  onDelete: () -> Unit
) {
  var showDeleteDialog by remember { mutableStateOf(false) }
  var markerPosition by remember { mutableStateOf<LatLng?>(null) }
  val context = LocalContext.current

  LaunchedEffect(event.address) {
    withContext(Dispatchers.IO) {
      try {
        @Suppress("DEPRECATION")
        val results = Geocoder(context, Locale.getDefault()).getFromLocationName(event.address, 1)
        if (!results.isNullOrEmpty()) {
          markerPosition = LatLng(results[0].latitude, results[0].longitude)
        }
      } catch (_: Exception) {}
    }
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      containerColor = EventoriasSurface,
      title = {
        Text(
          text = "Delete event",
          style = MaterialTheme.typography.titleLarge,
          color = EventoriasOnSurface
        )
      },
      text = {
        Text(
          text = "Are you sure you want to delete this event? This action cannot be undone.",
          style = MaterialTheme.typography.bodyMedium,
          color = EventoriasOnSurfaceMuted
        )
      },
      confirmButton = {
        TextButton(onClick = {
          showDeleteDialog = false
          onDelete()
        }) {
          Text("Delete", color = EventoriasPrimary)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel", color = EventoriasOnSurface)
        }
      }
    )
  }

  val zoned = remember(event.eventAt) {
    Instant.ofEpochSecond(event.eventAt.seconds).atZone(ZoneId.systemDefault())
  }
  val formattedDate = remember(zoned) { DATE_FORMATTER.format(zoned.toLocalDate()) }
  val formattedTime = remember(zoned) { TIME_FORMATTER.format(zoned.toLocalTime()) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(EventoriasBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(end = 20.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
          contentDescription = stringResource(R.string.back),
          tint = EventoriasOnSurface
        )
      }
      Text(
        text = event.title,
        style = MaterialTheme.typography.headlineMedium,
        color = EventoriasOnSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    // Scrollable content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      // Cover image
      AsyncImage(
        model = event.imageUrl,
        contentDescription = null,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
          .height(220.dp)
          .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Date + time + owner avatar (single row)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Outlined.CalendarMonth,
              contentDescription = null,
              tint = EventoriasOnSurface,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = formattedDate,
              style = MaterialTheme.typography.bodyLarge,
              color = EventoriasOnSurface
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Outlined.AccessTime,
              contentDescription = null,
              tint = EventoriasOnSurface,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = formattedTime,
              style = MaterialTheme.typography.bodyLarge,
              color = EventoriasOnSurface
            )
          }
        }
        if (event.ownerPhotoUrl != null) {
          AsyncImage(
            model = event.ownerPhotoUrl,
            contentDescription = null,
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = Modifier
              .size(56.dp)
              .background(EventoriasSurfaceMuted, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.PersonOutline,
              contentDescription = null,
              tint = EventoriasOnSurface,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Description
      Text(
        text = event.description,
        style = MaterialTheme.typography.bodyLarge,
        color = EventoriasOnSurface,
        modifier = Modifier.padding(horizontal = 20.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Address + map placeholder
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = event.address,
          style = MaterialTheme.typography.bodyLarge,
          color = EventoriasOnSurface,
          modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        val position = markerPosition
        if (position != null) {
          val cameraPositionState = rememberCameraPositionState {
            this.position = CameraPosition.fromLatLngZoom(position, 15f)
          }
          GoogleMap(
            modifier = Modifier
              .size(120.dp)
              .clip(RoundedCornerShape(8.dp)),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
              scrollGesturesEnabled = false,
              zoomGesturesEnabled = false,
              tiltGesturesEnabled = false,
              rotationGesturesEnabled = false,
              zoomControlsEnabled = false,
              mapToolbarEnabled = false,
              compassEnabled = false,
              myLocationButtonEnabled = false
            )
          ) {
            Marker(state = MarkerState(position = position))
          }
        } else {
          Box(
            modifier = Modifier
              .size(120.dp)
              .background(EventoriasSurfaceMuted, RoundedCornerShape(8.dp))
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }

    // Delete button
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
      EventoriasPrimaryButton(
        text = stringResource(R.string.event_delete_cta),
        onClick = { showDeleteDialog = true },
        modifier = Modifier.fillMaxWidth(),
        loading = isDeleting
      )
    }
  }
}
