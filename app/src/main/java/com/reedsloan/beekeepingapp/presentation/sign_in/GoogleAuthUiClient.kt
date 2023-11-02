package com.reedsloan.beekeepingapp.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.reedsloan.beekeepingapp.R
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val auth: FirebaseAuth,
) {
    /**
     * Starts the Google sign in flow.
     *
     * Returns an [IntentSender] that can be used to start the sign in flow.
     */
    suspend fun signIn(): Result<IntentSender> {
        return runCatching {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await().pendingIntent.intentSender
        }.onFailure {
            it.printStackTrace()
            if (it is CancellationException) throw it
        }
    }

    suspend fun signInWithIntent(intent: Intent): Result<SignInResult> {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return runCatching {
            val user = auth.signInWithCredential(googleCredentials).await().user

            user?.run {
                SignInResult(
                    GoogleUserData(
                        id = uid,
                        username = displayName ?: "",
                        photoUrl = photoUrl?.toString() ?: ""
                    ),
                    null
                )
            } ?: run {
                SignInResult(
                    GoogleUserData(
                        id = "",
                        username = "",
                        photoUrl = ""
                    ),
                    "User is null"
                )
            }
        }.onFailure {
            it.printStackTrace()
            if (it is CancellationException) throw it
        }
    }

    fun getSignedInUser(): GoogleUserData? = auth.currentUser?.run {
            GoogleUserData(
                id = uid,
                username = displayName ?: "",
                photoUrl = photoUrl?.toString() ?: ""
            )
        }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}