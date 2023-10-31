package com.reedsloan.beekeepingapp.presentation.sign_in

import com.reedsloan.beekeepingapp.data.UserPreferences

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val userPreferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = false,
)
