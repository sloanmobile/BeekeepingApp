package com.reedsloan.beekeepingapp.presentation.inspection_insights_screen

sealed class InspectionInsightsEvent {
    data object OnBackClicked : InspectionInsightsEvent()
    data object OnDeleteClicked : InspectionInsightsEvent()
}
