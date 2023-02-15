package com.reedsloan.beekeepingapp.presentation.common

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
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
        _selectedHour.value = dateTime.hour
        _selectedMinute.value = dateTime.minute
    }

    fun setSelectedHour(hour: Int) {
        // log the selected hour
        Log.d("CalendarViewModel", "Selected hour: $hour")
        _selectedHour.value = hour
    }

    fun setSelectedMinute(minute: Int) {
        _selectedMinute.value = minute
    }

    fun setIsPressed(isPressed: Boolean) {
        _isPressed.value = isPressed
    }
}