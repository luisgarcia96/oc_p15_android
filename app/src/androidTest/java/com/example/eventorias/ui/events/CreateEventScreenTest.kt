package com.example.eventorias.ui.events

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.SdkSuppress
import com.example.eventorias.events.CreateEventUiState
import com.example.eventorias.ui.theme.EventoriasTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 24, maxSdkVersion = 36)
class CreateEventScreenTest {
  @get:Rule
  val composeRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun createEventScreen_validateButtonInvokesCallback() {
    var validateInvoked = false

    composeRule.setContent {
      EventoriasTheme {
        CreateEventScreen(
          uiState = CreateEventUiState(),
          snackbarHostState = SnackbarHostState(),
          onTitleChanged = {},
          onDescriptionChanged = {},
          onDateChanged = {},
          onTimeChanged = {},
          onAddressChanged = {},
          onImageSelected = {},
          onBack = {},
          onValidate = { validateInvoked = true }
        )
      }
    }

    composeRule.onNodeWithText("Validate").performClick()
    composeRule.runOnIdle {
      assertTrue(validateInvoked)
    }
  }

  @Test
  fun createEventScreen_displaysValidationErrorFromState() {
    val errorMessage = "Please enter an event title."

    composeRule.setContent {
      EventoriasTheme {
        CreateEventScreen(
          uiState = CreateEventUiState(errorMessage = errorMessage),
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

    composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
  }
}
