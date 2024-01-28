package com.reedsloan.beekeepingapp.presentation.inspection_insights_screen

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InspectionsInsightsViewModel @Inject constructor(
    private val app: Application,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    private val _state = MutableStateFlow(InspectionInsightsScreenState())
    val state = _state.asStateFlow()

    fun onEvent(event: InspectionInsightsEvent, navController: NavController) {
        when (event) {
            is InspectionInsightsEvent.OnBackClicked -> {
                navController.popBackStack()
            }

            else -> {
                // Do nothing
            }
        }
    }

    fun initialize(selectedHiveId: String) {
        viewModelScope.launch {
            userDataRepository.getUserData().onSuccess { result ->
                _state.update {
                    it.copy(
                        userPreferences = result.userPreferences,
                        inspections = result.hives.first { hive ->
                            hive.id == selectedHiveId
                        }.hiveInspections
                    )
                }
            }.onFailure {
                Toast.makeText(app, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}