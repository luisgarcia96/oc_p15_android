package com.example.eventorias.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthRepository(
  private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
  fun observeCurrentUser(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      trySend(auth.currentUser)
    }

    firebaseAuth.addAuthStateListener(listener)
    trySend(firebaseAuth.currentUser)

    awaitClose { firebaseAuth.removeAuthStateListener(listener) }
  }

  suspend fun signInWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential).awaitResult()
  }

  suspend fun signInWithEmail(email: String, password: String) {
    firebaseAuth.signInWithEmailAndPassword(email, password).awaitResult()
  }

  suspend fun createAccount(email: String, password: String) {
    firebaseAuth.createUserWithEmailAndPassword(email, password).awaitResult()
  }

  fun signOut() {
    firebaseAuth.signOut()
  }

  fun describeError(throwable: Throwable): String = when (throwable) {
    is FirebaseAuthInvalidCredentialsException -> "That email or password is not valid."
    is FirebaseAuthInvalidUserException -> "No account was found for that email address."
    is FirebaseAuthUserCollisionException -> "An account already exists for that email address."
    is FirebaseNetworkException -> "Network error. Check your connection and try again."
    else -> throwable.message ?: "Authentication failed. Please try again."
  }
}
