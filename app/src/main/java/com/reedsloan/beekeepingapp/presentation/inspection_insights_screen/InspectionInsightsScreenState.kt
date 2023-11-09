package com.reedsloan.beekeepingapp.presentation.inspection_insights_screen

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.HiveInspection

data class InspectionInsightsScreenState(
    val inspections: List<HiveInspection> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences()
)