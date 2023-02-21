package com.reedsloan.beekeepingapp.presentation.common

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {
    private val _selectedHour = MutableStateFlow(0)
    val selectedHour: StateFlow<Int> = _selectedHour

    private val _selectedMinute = MutableStateFlow(0)
    val selectedMinute: StateFlow<Int> = _selectedMinute

    private val _isPressed = MutableStateFlow(false)
    val isPressed: StateFlow<Boolean> = _isPressed

    init {
        // update values with current time
        val dateTime: LocalDateTime = LocalDateTime.now()
        _selectedHour.update { dateTime.hour }
        _selectedMinute.update { dateTime.minute }
    }

    /**
     * Sets the selected hour.
     * @param hour The hour to set.
     */
    fun setSelectedHour(hour: Int) {
        _selectedHour.update { hour }
    }

    fun setSelectedMinute(minute: Int) {
        _selectedMinute.update { minute }
    }

    fun setIsPressed(isPressed: Boolean) {
        _isPressed.update { isPressed }
    }
}