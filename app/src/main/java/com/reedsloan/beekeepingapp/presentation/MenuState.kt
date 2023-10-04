package com.reedsloan.beekeepingapp.presentation

enum class MenuState {
    CLOSED,
    OPEN;

    fun toggle(): MenuState {
        return when (this) {
            CLOSED -> OPEN
            OPEN -> CLOSED
        }
    }

    fun isOpen(): Boolean {
        return this == OPEN
    }
}