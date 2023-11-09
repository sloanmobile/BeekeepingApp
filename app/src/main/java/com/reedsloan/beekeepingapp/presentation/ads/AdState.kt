package com.reedsloan.beekeepingapp.presentation.ads

data class AdState(
    val isAdFinished: Boolean = false,
    val isAdLoaded: Boolean = false,
    val isAdFailedToPlay: Boolean = false,
    val isAdPlaying: Boolean = false
)
