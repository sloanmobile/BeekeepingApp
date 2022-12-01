package com.reedsloan.beekeepingapp.presentation.home_screen

enum class MenuState {
    Closed,
    Open;

    fun toggle(): MenuState {
        return when (this) {
            Closed -> Open
            Open -> Closed
        }
    }
}