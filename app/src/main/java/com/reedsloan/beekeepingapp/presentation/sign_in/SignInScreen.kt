package com.reedsloan.beekeepingapp.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reedsloan.beekeepingapp.R

@Composable
fun SignInScreen(state: SignInState, onSignInClick: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val interactionSource = remember { MutableInteractionSource() }
        val indication = rememberRipple(color = Color.Black, bounded = true)

        // welcome to BeeLog - sign in with google
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 256.dp),
        ) {
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "BeeJournal",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }

        // if user is not signed up, show sign in button

        if (state.isSignInSuccessful || state.isLoading) {
            CircularProgressIndicator()
        } else {
            if (state.userPreferences.showSignUpWithGoogleButton) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_google_light_sq_su),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable(interactionSource = interactionSource, indication = indication) {
                            onSignInClick()
                        }
                        .indication(interactionSource = interactionSource, indication = indication)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_google_light_sq_si),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable(interactionSource = interactionSource, indication = indication) {
                            onSignInClick()
                        }
                        .indication(interactionSource = interactionSource, indication = indication)
                )
            }
        }
    }
}
