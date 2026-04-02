package com.example.eventorias.auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class GoogleAuthClient(
  private val appContext: Context
) {
  private val credentialManager = CredentialManager.create(appContext)

  suspend fun requestIdToken(activity: ComponentActivity): String {
    return runCatching {
      getGoogleIdToken(activity, filterByAuthorizedAccounts = true)
    }.getOrElse { throwable ->
      if (throwable is NoCredentialException) {
        getGoogleIdTokenFromButtonFlow(activity)
      } else {
        throw throwable
      }
    }
  }

  suspend fun clearCredentialState() {
    credentialManager.clearCredentialState(ClearCredentialStateRequest())
  }

  fun describeError(throwable: Throwable): String = when (throwable) {
    is NoCredentialException -> "No Google account is available on this device."
    is GetCredentialException -> "Google sign-in was canceled or could not be completed."
    is GoogleIdTokenParsingException -> "Google sign-in returned an invalid token."
    else -> throwable.message ?: "Google sign-in failed. Please try again."
  }

  private suspend fun getGoogleIdToken(
    activity: ComponentActivity,
    filterByAuthorizedAccounts: Boolean
  ): String {
    val webClientId = appContext.lookupWebClientId()
    val googleIdOption = GetGoogleIdOption.Builder()
      .setServerClientId(webClientId)
      .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
      .setAutoSelectEnabled(filterByAuthorizedAccounts)
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    val result = credentialManager.getCredential(
      context = activity,
      request = request
    )

    val credential = result.credential
    if (credential is CustomCredential &&
      credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
      return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    error("Unexpected credential type returned from Google sign-in.")
  }

  private suspend fun getGoogleIdTokenFromButtonFlow(
    activity: ComponentActivity
  ): String {
    val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
      serverClientId = appContext.lookupWebClientId()
    ).build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(signInWithGoogleOption)
      .build()

    val result = credentialManager.getCredential(
      context = activity,
      request = request
    )

    val credential = result.credential
    if (credential is CustomCredential &&
      credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
      return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    error("Unexpected credential type returned from Google sign-in.")
  }

  private fun Context.lookupWebClientId(): String {
    val resourceId = resources.getIdentifier("default_web_client_id", "string", packageName)
    check(resourceId != 0) {
      "Google sign-in is not configured yet. Enable the Google provider in Firebase Authentication and download an updated google-services.json."
    }
    return getString(resourceId)
  }
}
