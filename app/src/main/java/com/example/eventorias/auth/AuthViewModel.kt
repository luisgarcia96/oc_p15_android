package com.example.eventorias.auth

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthRoute {
  Login,
  EmailAuth,
  SignedIn
}

enum class EmailAuthMode {
  SIGN_IN,
  SIGN_UP
}

enum class AuthAction {
  GOOGLE,
  EMAIL_SIGN_IN,
  EMAIL_SIGN_UP,
  SIGN_OUT
}

data class AuthUiState(
  val currentUser: FirebaseUser? = null,
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val currentRoute: AuthRoute = AuthRoute.Login,
  val emailAuthMode: EmailAuthMode = EmailAuthMode.SIGN_IN,
  val inFlightAction: AuthAction? = null,
  val email: String = "",
  val password: String = ""
)

private data class AuthFormState(
  val route: AuthRoute = AuthRoute.Login,
  val emailAuthMode: EmailAuthMode = EmailAuthMode.SIGN_IN,
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val inFlightAction: AuthAction? = null,
  val email: String = "",
  val password: String = ""
)

class AuthViewModel(
  application: Application
) : AndroidViewModel(application) {
  private val repository = AuthRepository()
  private val googleAuthClient = GoogleAuthClient(application.applicationContext)
  private val formState = MutableStateFlow(AuthFormState())

  val uiState: StateFlow<AuthUiState> = combine(
    repository.observeCurrentUser(),
    formState
  ) { user, form ->
    AuthUiState(
      currentUser = user,
      isLoading = form.isLoading,
      errorMessage = form.errorMessage,
      currentRoute = if (user != null) AuthRoute.SignedIn else form.route,
      emailAuthMode = form.emailAuthMode,
      inFlightAction = form.inFlightAction,
      email = form.email,
      password = form.password
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = AuthUiState()
  )

  fun openEmailAuth() {
    formState.update {
      it.copy(route = AuthRoute.EmailAuth, errorMessage = null)
    }
  }

  fun backToLogin() {
    formState.update {
      it.copy(route = AuthRoute.Login, errorMessage = null, password = "")
    }
  }

  fun updateEmail(value: String) {
    formState.update { it.copy(email = value, errorMessage = null) }
  }

  fun updatePassword(value: String) {
    formState.update { it.copy(password = value, errorMessage = null) }
  }

  fun toggleEmailAuthMode() {
    formState.update {
      it.copy(
        emailAuthMode = if (it.emailAuthMode == EmailAuthMode.SIGN_IN) {
          EmailAuthMode.SIGN_UP
        } else {
          EmailAuthMode.SIGN_IN
        },
        errorMessage = null
      )
    }
  }

  fun signInWithGoogle(activity: ComponentActivity) {
    viewModelScope.launch {
      beginLoading(AuthAction.GOOGLE)

      runCatching {
        val idToken = googleAuthClient.requestIdToken(activity)
        repository.signInWithGoogle(idToken)
      }.onSuccess {
        finishLoading()
      }.onFailure { throwable ->
        formState.update {
          it.copy(
            isLoading = false,
            inFlightAction = null,
            errorMessage = googleAuthClient.describeError(throwable)
          )
        }
      }
    }
  }

  fun submitEmailAuth() {
    val snapshot = formState.value
    val validationError = validate(snapshot.email, snapshot.password, snapshot.emailAuthMode)
    if (validationError != null) {
      formState.update { it.copy(errorMessage = validationError) }
      return
    }

    viewModelScope.launch {
      val action = if (snapshot.emailAuthMode == EmailAuthMode.SIGN_IN) {
        AuthAction.EMAIL_SIGN_IN
      } else {
        AuthAction.EMAIL_SIGN_UP
      }
      beginLoading(action)

      runCatching {
        if (snapshot.emailAuthMode == EmailAuthMode.SIGN_IN) {
          repository.signInWithEmail(snapshot.email.trim(), snapshot.password)
        } else {
          repository.createAccount(snapshot.email.trim(), snapshot.password)
        }
      }.onSuccess {
        finishLoading(clearPassword = true)
      }.onFailure { throwable ->
        formState.update {
          it.copy(
            isLoading = false,
            inFlightAction = null,
            errorMessage = repository.describeError(throwable)
          )
        }
      }
    }
  }

  fun signOut() {
    viewModelScope.launch {
      beginLoading(AuthAction.SIGN_OUT)
      runCatching {
        googleAuthClient.clearCredentialState()
        repository.signOut()
      }.onSuccess {
        formState.update {
          it.copy(
            route = AuthRoute.Login,
            isLoading = false,
            inFlightAction = null,
            errorMessage = null,
            password = ""
          )
        }
      }.onFailure { throwable ->
        formState.update {
          it.copy(
            isLoading = false,
            inFlightAction = null,
            errorMessage = googleAuthClient.describeError(throwable)
          )
        }
      }
    }
  }

  private fun beginLoading(action: AuthAction) {
    formState.update {
      it.copy(
        isLoading = true,
        inFlightAction = action,
        errorMessage = null
      )
    }
  }

  private fun finishLoading(clearPassword: Boolean = false) {
    formState.update {
      it.copy(
        isLoading = false,
        inFlightAction = null,
        errorMessage = null,
        password = if (clearPassword) "" else it.password
      )
    }
  }

  private fun validate(
    email: String,
    password: String,
    mode: EmailAuthMode
  ): String? {
    val trimmedEmail = email.trim()
    if (trimmedEmail.isEmpty()) {
      return "Enter your email address."
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
      return "Enter a valid email address."
    }
    if (password.isBlank()) {
      return "Enter your password."
    }
    if (mode == EmailAuthMode.SIGN_UP && password.length < 6) {
      return "Password must be at least 6 characters."
    }
    return null
  }
}
