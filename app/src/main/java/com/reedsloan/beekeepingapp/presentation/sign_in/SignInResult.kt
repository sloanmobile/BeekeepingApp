package com.reedsloan.beekeepingapp.presentation.sign_in

data class SignInResult(
    val data: GoogleUserData? = null,
    val error: String?
)

data class GoogleUserData(
    val id: String,
    val username: String,
    val photoUrl: String
)