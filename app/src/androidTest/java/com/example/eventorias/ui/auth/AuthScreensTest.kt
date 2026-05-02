package com.example.eventorias.ui.auth

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.SdkSuppress
import com.example.eventorias.auth.AuthUiState
import com.example.eventorias.auth.EmailAuthMode
import com.example.eventorias.ui.theme.EventoriasTheme
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 24, maxSdkVersion = 36)
class AuthScreensTest {
  @get:Rule
  val composeRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun loginScreen_displaysGoogleAndEmailActions() {
    composeRule.setContent {
      EventoriasTheme {
        LoginScreen(
          uiState = AuthUiState(),
          onGoogleClick = {},
          onEmailClick = {}
        )
      }
    }

    composeRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    composeRule.onNodeWithText("Sign in with email").assertIsDisplayed()
  }

  @Test
  fun emailAuthScreen_toggleSwitchesToSignUpMode() {
    composeRule.setContent {
      var uiState by mutableStateOf(
        AuthUiState(emailAuthMode = EmailAuthMode.SIGN_IN)
      )

      EventoriasTheme {
        EmailAuthScreen(
          uiState = uiState,
          onBack = {},
          onEmailChanged = {},
          onPasswordChanged = {},
          onSubmit = {},
          onToggleMode = {
            uiState = uiState.copy(
              emailAuthMode = if (uiState.emailAuthMode == EmailAuthMode.SIGN_IN) {
                EmailAuthMode.SIGN_UP
              } else {
                EmailAuthMode.SIGN_IN
              }
            )
          }
        )
      }
    }

    composeRule.onNodeWithText("Sign in with email").assertIsDisplayed()
    composeRule.onNodeWithText("Need an account? Create one").performClick()
    composeRule.onNodeWithText("Create your account").assertIsDisplayed()
    composeRule.onNodeWithText("Already have an account? Sign in").assertIsDisplayed()
  }
}
