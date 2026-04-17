package com.example.eventorias

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventorias.auth.AuthRoute
import com.example.eventorias.auth.AuthViewModel
import com.example.eventorias.ui.auth.EmailAuthScreen
import com.example.eventorias.ui.auth.LoginScreen
import com.example.eventorias.ui.events.EventsHomeScreen
import com.example.eventorias.ui.theme.EventoriasTheme
import com.example.eventorias.notifications.NotificationHelper
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
  private var notificationsEnabled by mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
      PlayIntegrityAppCheckProviderFactory.getInstance()
    )
    NotificationHelper.createDefaultChannel(this)
    syncNotificationsEnabled()
    logFcmToken()
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
    )
    setContent {
      EventoriasTheme {
        val viewModel: AuthViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val activity = remember(context) { context as? ComponentActivity }
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
          ActivityResultContracts.RequestPermission()
        ) {
          syncNotificationsEnabled()
        }

        when (uiState.currentRoute) {
          AuthRoute.Login -> LoginScreen(
            uiState = uiState,
            onGoogleClick = { activity?.let(viewModel::signInWithGoogle) },
            onEmailClick = viewModel::openEmailAuth
          )

          AuthRoute.EmailAuth -> EmailAuthScreen(
            uiState = uiState,
            onBack = viewModel::backToLogin,
            onEmailChanged = viewModel::updateEmail,
            onPasswordChanged = viewModel::updatePassword,
            onSubmit = viewModel::submitEmailAuth,
            onToggleMode = viewModel::toggleEmailAuthMode
          )

          AuthRoute.SignedIn -> EventsHomeScreen(
            uiState = uiState,
            onSignOut = viewModel::signOut,
            notificationsEnabled = notificationsEnabled,
            onNotificationsToggle = { enabled ->
              if (enabled) {
                if (NotificationHelper.hasPostNotificationsPermission(this)) {
                  if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    syncNotificationsEnabled()
                  } else {
                    openNotificationSettings()
                  }
                } else {
                  notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
              } else {
                openNotificationSettings()
              }
            }
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    syncNotificationsEnabled()
  }

  private fun syncNotificationsEnabled() {
    notificationsEnabled = NotificationHelper.canPostNotifications(this)
  }

  private fun logFcmToken() {
    FirebaseMessaging.getInstance().token
      .addOnSuccessListener { token ->
        Log.d(TAG, "FCM token: $token")
      }
      .addOnFailureListener { error ->
        Log.w(TAG, "Unable to fetch the FCM token.", error)
      }
  }

  private fun openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
      putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
    startActivity(intent)
  }

  companion object {
    private const val TAG = "MainActivity"
  }
}
