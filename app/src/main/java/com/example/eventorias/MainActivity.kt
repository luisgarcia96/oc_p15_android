package com.example.eventorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventorias.auth.AuthRoute
import com.example.eventorias.auth.AuthViewModel
import com.example.eventorias.ui.auth.EmailAuthScreen
import com.example.eventorias.ui.auth.LoginScreen
import com.example.eventorias.ui.auth.SignedInScreen
import com.example.eventorias.ui.theme.EventoriasTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EventoriasTheme {
        val viewModel: AuthViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val activity = remember(context) { context as? ComponentActivity }

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

          AuthRoute.SignedIn -> SignedInScreen(
            uiState = uiState,
            onSignOut = viewModel::signOut
          )
        }
      }
    }
  }
}
