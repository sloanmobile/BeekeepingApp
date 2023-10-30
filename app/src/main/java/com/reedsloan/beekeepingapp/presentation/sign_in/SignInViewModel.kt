package com.reedsloan.beekeepingapp.presentation.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state

    fun onSignInResult(signInResult: Result<SignInResult>) {
        signInResult.onSuccess {
            _state.update {
                SignInState(
                    isSignInSuccessful = true,
                    signInError = null
                )
            }
        }.onFailure {
            _state.update {
                SignInState(
                    signInError = it.signInError,
                    isSignInSuccessful = false
                )
            }
        }
    }

    fun resetState() {
        _state.update {
            SignInState()
        }
    }
}