package com.reedsloan.beekeepingapp.presentation.sign_in

data class SignInResult(
    val data: UserData? = null,
    val error: String?
)

data class UserData(
    val id: String,
    val username: String,
    val photoUrl: String
)