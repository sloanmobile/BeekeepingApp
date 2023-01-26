package com.reedsloan.beekeepingapp.presentation.home_screen

enum class MenuState {
    CLOSED,
    OPEN;

    fun toggle(): MenuState {
        return when (this) {
            CLOSED -> OPEN
            OPEN -> CLOSED
        }
    }
}