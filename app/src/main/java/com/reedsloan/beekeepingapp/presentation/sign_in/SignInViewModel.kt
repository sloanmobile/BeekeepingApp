package com.reedsloan.beekeepingapp.presentation.sign_in

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.domain.repo.LocalUserDataRepository
import com.reedsloan.beekeepingapp.presentation.common.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val app: Application,
    private val localUserDataRepository: LocalUserDataRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state

    init {
        viewModelScope.launch {
            getUserDataFromLocal()
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
        viewModelScope.launch {
            saveUserDataToLocal(UserData(userPreferences = state.value.userPreferences))
        }
    }

    private fun resetState() {
        _state.update {
            SignInState(
                // We need to keep this value when resetting the state
                userPreferences = state.value.userPreferences
            )
        }
    }

    private suspend fun saveUserDataToLocal(userData: UserData) {
        localUserDataRepository.updateUserData(userData)
    }

    private suspend fun getUserDataFromLocal() {
        localUserDataRepository.getUserData().onSuccess {userData ->
            _state.update {
                it.copy(
                    isLoading = false,
                    userPreferences = userData.userPreferences
                )
            }
        }
    }
}