package com.reedsloan.beekeepingapp.presentation.sign_in

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.presentation.common.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val app: Application,
    private val hiveRepository: HiveRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state

    init {
        viewModelScope.launch {
            getUserPreferences()
        }
    }

    fun onSignInClick() {
        _state.update { it.copy(isLoading = true) }
    }

    fun signOut(navController: NavController) {
        firebaseAuth.signOut()
        // reset state
        resetState()

        // pop back stack to prevent user from navigating back to the home screen
        navController.popBackStack()
        navController.navigate(Screen.SignInScreen.route)
    }

    fun onSignInResult(signInResult: Result<SignInResult>) {
        signInResult.onSuccess {
            _state.update {
                SignInState(
                    isSignInSuccessful = true,
                    signInError = null,
                    userPreferences = it.userPreferences.copy(
                        showSignUpWithGoogleButton = false
                    ),
                    isLoading = false
                )
            }
        }.onFailure {
            Toast.makeText(app, "Error signing in", Toast.LENGTH_SHORT).show()
        }
        updateUserPreferences(state.value.userPreferences)
    }

    private fun resetState() {
        _state.update {
            SignInState(
                // We need to keep this value when resetting the state
                userPreferences = state.value.userPreferences
            )
        }
    }

    private suspend fun getUserPreferences() {
        runCatching { hiveRepository.getUserPreferences() }.onSuccess { userPreferences ->
            _state.update { it.copy(userPreferences = userPreferences) }
        }
    }

    private fun updateUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            runCatching { hiveRepository.updateUserPreferences(userPreferences) }.onSuccess {
                _state.update { state.value.copy(userPreferences = userPreferences) }
            }.onFailure {
                Toast.makeText(
                    app, "Error updating user data", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}