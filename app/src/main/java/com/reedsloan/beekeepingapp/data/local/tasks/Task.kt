package com.reedsloan.beekeepingapp.data.local.tasks

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID,
    val title: String,
    val date: String,
    val showReminderNotification: Boolean,
    val description: String,
    val isCompleted: Boolean,
    val category: String
) {
    companion object {
        fun Task.isToday(): Boolean {
            return LocalDate.now().equals(LocalDate.parse(date))
        }
    }
}
