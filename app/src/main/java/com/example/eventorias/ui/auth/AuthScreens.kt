package com.example.eventorias.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.eventorias.ui.theme.GoogleBlue
import com.example.eventorias.ui.theme.GoogleGreen
import com.example.eventorias.ui.theme.GoogleRed
import com.example.eventorias.ui.theme.GoogleYellow

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
        .padding(horizontal = 28.dp),
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
          contentDescription = "Back"
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = if (isSignIn) "Sign in with email" else "Create your account",
        style = MaterialTheme.typography.headlineMedium,
        color = EventoriasOnSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Use Firebase Authentication with email and password.",
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
        label = "Email",
        placeholder = "you@example.com",
        keyboardType = KeyboardType.Email
      )
      Spacer(modifier = Modifier.height(16.dp))
      AuthTextField(
        value = uiState.password,
        onValueChange = onPasswordChanged,
        label = "Password",
        placeholder = "At least 6 characters",
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
        shape = RoundedCornerShape(16.dp),
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
            text = if (isSignIn) "Sign in" else "Create account",
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
          text = if (isSignIn) {
            "Need an account? Create one"
          } else {
            "Already have an account? Sign in"
          },
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
  val userLabel = uiState.currentUser?.email ?: uiState.currentUser?.displayName ?: "Signed in with Google"

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
            text = "You are signed in",
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
              Text("Sign out", style = MaterialTheme.typography.labelLarge)
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
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    PlaceholderLogo(compact = compact)
    Spacer(modifier = Modifier.height(if (compact) 20.dp else 28.dp))
    Text(
      text = "EVENTORIAS",
      style = MaterialTheme.typography.displayMedium.copy(
        fontSize = if (compact) 26.sp else 34.sp
      ),
      color = EventoriasOnSurface
    )
  }
}

@Composable
private fun PlaceholderLogo(compact: Boolean) {
  val size = if (compact) 88.dp else 112.dp
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .border(
        border = BorderStroke(1.2.dp, EventoriasDivider),
        shape = CircleShape
      )
      .background(EventoriasSurface.copy(alpha = 0.32f)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(if (compact) 42.dp else 56.dp)) {
      val canvasSize = minOf(this.size.width, this.size.height)
      val stroke = canvasSize / 12f
      drawCircle(
        color = EventoriasOnSurface,
        radius = canvasSize * 0.28f,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
      )
      drawCircle(
        color = EventoriasBackground,
        radius = canvasSize * 0.18f,
        center = Offset(this.size.width * 0.57f, this.size.height * 0.43f)
      )
      val rayColor = EventoriasOnSurface
      val center = Offset(this.size.width / 2f, this.size.height / 2f)
      repeat(8) { index ->
        val angle = Math.toRadians((index * 45.0) - 90.0)
        val start = Offset(
          x = center.x + kotlin.math.cos(angle).toFloat() * canvasSize * 0.36f,
          y = center.y + kotlin.math.sin(angle).toFloat() * canvasSize * 0.36f
        )
        val end = Offset(
          x = center.x + kotlin.math.cos(angle).toFloat() * canvasSize * 0.48f,
          y = center.y + kotlin.math.sin(angle).toFloat() * canvasSize * 0.48f
        )
        drawLine(
          color = rayColor,
          start = start,
          end = end,
          strokeWidth = stroke * 0.72f,
          cap = StrokeCap.Round
        )
      }
    }
  }
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
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = EventoriasCard,
      contentColor = EventoriasCardText,
      disabledContainerColor = EventoriasCard.copy(alpha = 0.8f),
      disabledContentColor = EventoriasCardText.copy(alpha = 0.7f)
    )
  ) {
    GoogleGlyph()
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
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = EventoriasPrimary,
      contentColor = EventoriasOnPrimary,
      disabledContainerColor = EventoriasPrimary.copy(alpha = 0.45f)
    )
  ) {
    Icon(
      imageVector = Icons.Rounded.Email,
      contentDescription = null
    )
    Spacer(modifier = Modifier.size(14.dp))
    Text(
      text = "Sign in with email",
      style = MaterialTheme.typography.labelLarge
    )
  }
}

@Composable
private fun GoogleGlyph() {
  Row(
    horizontalArrangement = Arrangement.spacedBy(1.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "G",
      color = GoogleBlue,
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif
      )
    )
    Box(
      modifier = Modifier
        .size(width = 4.dp, height = 4.dp)
        .background(GoogleRed, CircleShape)
    )
    Box(
      modifier = Modifier
        .size(width = 4.dp, height = 4.dp)
        .background(GoogleYellow, CircleShape)
    )
    Box(
      modifier = Modifier
        .size(width = 4.dp, height = 4.dp)
        .background(GoogleGreen, CircleShape)
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
      .clip(RoundedCornerShape(18.dp))
      .background(EventoriasSurface)
      .padding(6.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    ModeOption(
      text = "Sign in",
      selected = isSignIn,
      enabled = enabled,
      onClick = { if (!isSignIn) onToggleMode() },
      modifier = Modifier.weight(1f),
      interactionSource = interactionSource
    )
    ModeOption(
      text = "Create account",
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
      .clip(RoundedCornerShape(14.dp))
      .background(if (selected) EventoriasPrimary else Color.Transparent)
      .clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
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
    shape = RoundedCornerShape(18.dp)
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
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
      Text(
        text = "Authentication issue",
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
