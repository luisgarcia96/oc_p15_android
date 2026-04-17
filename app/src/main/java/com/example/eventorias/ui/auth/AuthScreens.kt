package com.example.eventorias.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.auth.AuthAction
import com.example.eventorias.auth.AuthUiState
import com.example.eventorias.auth.EmailAuthMode
import com.example.eventorias.ui.theme.EventoriasBackground
import com.example.eventorias.ui.theme.EventoriasCard
import com.example.eventorias.ui.theme.EventoriasCardText
import com.example.eventorias.ui.theme.EventoriasDivider
import com.example.eventorias.ui.theme.EventoriasOnPrimary
import com.example.eventorias.ui.theme.EventoriasOnSurface
import com.example.eventorias.ui.theme.EventoriasOnSurfaceMuted
import com.example.eventorias.ui.theme.EventoriasPrimary
import com.example.eventorias.ui.theme.EventoriasSurface
import com.example.eventorias.ui.theme.EventoriasTheme

@Composable
fun LoginScreen(
  uiState: AuthUiState,
  onGoogleClick: () -> Unit,
  onEmailClick: () -> Unit
) {
  AuthScaffold {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 72.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      BrandHeader()
      Spacer(modifier = Modifier.height(58.dp))
      GoogleButton(
        loading = uiState.isLoading && uiState.inFlightAction == AuthAction.GOOGLE,
        enabled = !uiState.isLoading,
        onClick = onGoogleClick
      )
      Spacer(modifier = Modifier.height(22.dp))
      EmailButton(
        enabled = !uiState.isLoading,
        onClick = onEmailClick
      )
      AuthError(message = uiState.errorMessage)
    }
  }
}

@Composable
fun EmailAuthScreen(
  uiState: AuthUiState,
  onBack: () -> Unit,
  onEmailChanged: (String) -> Unit,
  onPasswordChanged: (String) -> Unit,
  onSubmit: () -> Unit,
  onToggleMode: () -> Unit
) {
  val isSignIn = uiState.emailAuthMode == EmailAuthMode.SIGN_IN
  AuthScaffold {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.Start
    ) {
      FilledIconButton(
        onClick = onBack,
        modifier = Modifier.statusBarsPadding(),
        enabled = !uiState.isLoading,
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = EventoriasSurface,
          contentColor = EventoriasOnSurface
        )
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
          contentDescription = stringResource(R.string.back)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = stringResource(
          if (isSignIn) R.string.email_sign_in_title else R.string.email_sign_up_title
        ),
        style = MaterialTheme.typography.headlineMedium,
        color = EventoriasOnSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.email_auth_firebase_description),
        style = MaterialTheme.typography.bodyLarge,
        color = EventoriasOnSurfaceMuted
      )
      Spacer(modifier = Modifier.height(28.dp))

      ModeToggle(
        isSignIn = isSignIn,
        enabled = !uiState.isLoading,
        onToggleMode = onToggleMode
      )

      Spacer(modifier = Modifier.height(24.dp))

      AuthTextField(
        value = uiState.email,
        onValueChange = onEmailChanged,
        label = stringResource(R.string.email_label),
        placeholder = stringResource(R.string.email_placeholder),
        keyboardType = KeyboardType.Email
      )
      Spacer(modifier = Modifier.height(16.dp))
      AuthTextField(
        value = uiState.password,
        onValueChange = onPasswordChanged,
        label = stringResource(R.string.password_label),
        placeholder = stringResource(R.string.password_placeholder),
        keyboardType = KeyboardType.Password,
        isPassword = true
      )
      AuthError(message = uiState.errorMessage, topPadding = 16.dp)

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = onSubmit,
        enabled = !uiState.isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = EventoriasPrimary,
          contentColor = EventoriasOnPrimary,
          disabledContainerColor = EventoriasPrimary.copy(alpha = 0.45f),
          disabledContentColor = EventoriasOnPrimary.copy(alpha = 0.8f)
        )
      ) {
        if (uiState.isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = EventoriasOnPrimary
          )
        } else {
          Text(
            text = stringResource(if (isSignIn) R.string.sign_in else R.string.create_account),
            style = MaterialTheme.typography.labelLarge
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      TextButton(
        onClick = onToggleMode,
        enabled = !uiState.isLoading,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Text(
          text = stringResource(
            if (isSignIn) R.string.switch_to_sign_up else R.string.switch_to_sign_in
          ),
          color = EventoriasOnSurface
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun SignedInScreen(
  uiState: AuthUiState,
  onSignOut: () -> Unit
) {
  val userLabel = uiState.currentUser?.email
    ?: uiState.currentUser?.displayName
    ?: stringResource(R.string.signed_out_user)

  AuthScaffold {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      BrandHeader(compact = true)
      Spacer(modifier = Modifier.height(40.dp))
      Surface(
        shape = RoundedCornerShape(28.dp),
        color = EventoriasSurface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = stringResource(R.string.signed_in_title),
            style = MaterialTheme.typography.headlineMedium,
            color = EventoriasOnSurface,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = userLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = EventoriasOnSurfaceMuted,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(24.dp))
          Button(
            onClick = onSignOut,
            enabled = !uiState.isLoading,
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = EventoriasPrimary,
              contentColor = EventoriasOnPrimary
            )
          ) {
            if (uiState.isLoading) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = EventoriasOnPrimary
              )
            } else {
              Text(
                text = stringResource(R.string.sign_out),
                style = MaterialTheme.typography.labelLarge
              )
            }
          }
        }
      }
      AuthError(message = uiState.errorMessage)
    }
  }
}

@Composable
private fun AuthScaffold(content: @Composable () -> Unit) {
  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = EventoriasBackground
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
        .navigationBarsPadding()
    ) {
      content()
    }
  }
}

@Composable
private fun BrandHeader(compact: Boolean = false) {
  Image(
    painter = painterResource(id = R.drawable.eventorias_logo),
    contentDescription = null,
    modifier = Modifier.size(
      width = if (compact) 220.dp else 280.dp,
      height = if (compact) 140.dp else 176.dp
    ),
    contentScale = ContentScale.Fit
  )
}

@Composable
private fun GoogleButton(
  loading: Boolean,
  enabled: Boolean,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
      .fillMaxWidth()
      .height(58.dp),
    shape = RoundedCornerShape(4.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = EventoriasCard,
      contentColor = EventoriasCardText,
      disabledContainerColor = EventoriasCard.copy(alpha = 0.8f),
      disabledContentColor = EventoriasCardText.copy(alpha = 0.7f)
    )
  ) {
    Image(
      painter = painterResource(id = R.drawable.google_icon),
      contentDescription = null,
      modifier = Modifier.size(24.dp),
      contentScale = ContentScale.Fit
    )
    Spacer(modifier = Modifier.size(14.dp))
    if (loading) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        strokeWidth = 2.dp,
        color = EventoriasCardText
      )
    } else {
      Text(
        text = "Sign in with Google",
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
      )
    }
  }
}

@Composable
private fun EmailButton(
  enabled: Boolean,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
      .fillMaxWidth()
      .height(58.dp),
    shape = RoundedCornerShape(4.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = EventoriasPrimary,
      contentColor = EventoriasOnPrimary,
      disabledContainerColor = EventoriasPrimary.copy(alpha = 0.45f)
    )
  ) {
    Image(
      painter = painterResource(id = R.drawable.paper_letter_icon),
      contentDescription = null,
      modifier = Modifier.size(24.dp),
      contentScale = ContentScale.Fit
    )
    Spacer(modifier = Modifier.size(14.dp))
    Text(
      text = "Sign in with email",
      style = MaterialTheme.typography.labelLarge
    )
  }
}

@Composable
private fun ModeToggle(
  isSignIn: Boolean,
  enabled: Boolean,
  onToggleMode: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(4.dp))
      .background(EventoriasSurface)
      .padding(6.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    ModeOption(
      text = stringResource(R.string.email_auth_mode_sign_in),
      selected = isSignIn,
      enabled = enabled,
      onClick = { if (!isSignIn) onToggleMode() },
      modifier = Modifier.weight(1f),
      interactionSource = interactionSource
    )
    ModeOption(
      text = stringResource(R.string.email_auth_mode_sign_up),
      selected = !isSignIn,
      enabled = enabled,
      onClick = { if (isSignIn) onToggleMode() },
      modifier = Modifier.weight(1f),
      interactionSource = interactionSource
    )
  }
}

@Composable
private fun ModeOption(
  text: String,
  selected: Boolean,
  enabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier,
  interactionSource: MutableInteractionSource
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(4.dp))
      .background(if (selected) EventoriasPrimary else Color.Transparent)
      .clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null,
        role = Role.Tab,
        onClick = onClick
      )
      .semantics(mergeDescendants = true) {
        this.selected = selected
      }
      .padding(vertical = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = if (selected) EventoriasOnPrimary else EventoriasOnSurfaceMuted,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
      )
    )
  }
}

@Composable
private fun AuthTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  keyboardType: KeyboardType,
  isPassword: Boolean = false
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = {
      Text(
        text = placeholder,
        color = EventoriasOnSurfaceMuted
      )
    },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
    colors = TextFieldDefaults.colors(
      focusedContainerColor = EventoriasSurface,
      unfocusedContainerColor = EventoriasSurface,
      disabledContainerColor = EventoriasSurface,
      focusedTextColor = EventoriasOnSurface,
      unfocusedTextColor = EventoriasOnSurface,
      focusedLabelColor = EventoriasOnSurface,
      unfocusedLabelColor = EventoriasOnSurfaceMuted,
      focusedIndicatorColor = EventoriasPrimary,
      unfocusedIndicatorColor = EventoriasDivider,
      cursorColor = EventoriasPrimary
    ),
    shape = RoundedCornerShape(4.dp)
  )
}

@Composable
private fun AuthError(
  message: String?,
  topPadding: androidx.compose.ui.unit.Dp = 24.dp
) {
  if (message.isNullOrBlank()) {
    return
  }

  Spacer(modifier = Modifier.height(topPadding))
  Surface(
    color = EventoriasPrimary.copy(alpha = 0.12f),
    shape = RoundedCornerShape(4.dp),
    modifier = Modifier
      .fillMaxWidth()
      .semantics(mergeDescendants = true) {
        liveRegion = LiveRegionMode.Assertive
      }
  ) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
      Text(
        text = stringResource(R.string.authentication_issue),
        style = MaterialTheme.typography.titleMedium,
        color = EventoriasOnSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      HorizontalDivider(color = EventoriasDivider)
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = EventoriasOnSurfaceMuted
      )
    }
  }
}

@Preview(name = "Login", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun LoginScreenPreview() {
  EventoriasTheme {
    LoginScreen(
      uiState = AuthUiState(),
      onGoogleClick = {},
      onEmailClick = {}
    )
  }
}

@Preview(name = "Login Loading", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun LoginScreenLoadingPreview() {
  EventoriasTheme {
    LoginScreen(
      uiState = AuthUiState(
        isLoading = true,
        inFlightAction = AuthAction.GOOGLE
      ),
      onGoogleClick = {},
      onEmailClick = {}
    )
  }
}

@Preview(name = "Email Auth", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun EmailAuthScreenPreview() {
  EventoriasTheme {
    EmailAuthScreen(
      uiState = AuthUiState(
        currentRoute = com.example.eventorias.auth.AuthRoute.EmailAuth,
        email = "hello@eventorias.com",
        password = "secret123",
        emailAuthMode = EmailAuthMode.SIGN_IN
      ),
      onBack = {},
      onEmailChanged = {},
      onPasswordChanged = {},
      onSubmit = {},
      onToggleMode = {}
    )
  }
}

@Preview(name = "Email Auth Error", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun EmailAuthErrorPreview() {
  EventoriasTheme {
    EmailAuthScreen(
      uiState = AuthUiState(
        currentRoute = com.example.eventorias.auth.AuthRoute.EmailAuth,
        email = "hello@eventorias.com",
        password = "123",
        emailAuthMode = EmailAuthMode.SIGN_UP,
        errorMessage = "Password must be at least 6 characters."
      ),
      onBack = {},
      onEmailChanged = {},
      onPasswordChanged = {},
      onSubmit = {},
      onToggleMode = {}
    )
  }
}

@Preview(name = "Signed In", showBackground = true, backgroundColor = 0xFF1E1A20)
@Composable
private fun SignedInScreenPreview() {
  EventoriasTheme {
    SignedInScreen(
      uiState = AuthUiState(),
      onSignOut = {}
    )
  }
}
