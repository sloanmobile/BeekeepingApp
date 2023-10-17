package com.reedsloan.beekeepingapp.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.reedsloan.beekeepingapp.data.local.hive.BroodStage
import com.reedsloan.beekeepingapp.data.local.hive.EquipmentCondition
import com.reedsloan.beekeepingapp.data.local.hive.FoundationType
import com.reedsloan.beekeepingapp.data.local.hive.FramesAndCombs
import com.reedsloan.beekeepingapp.data.local.hive.LayingPattern
import com.reedsloan.beekeepingapp.data.local.hive.Odor
import com.reedsloan.beekeepingapp.data.local.hive.Population
import com.reedsloan.beekeepingapp.data.local.hive.QueenCells
import com.reedsloan.beekeepingapp.data.local.hive.QueenMarker
import com.reedsloan.beekeepingapp.data.local.hive.Temperament
import com.reedsloan.beekeepingapp.data.local.hive.Weather
import com.reedsloan.beekeepingapp.presentation.common.DataEntryChip
import com.reedsloan.beekeepingapp.presentation.viewmodel.HiveViewModel
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogScreen(navController: NavController, hiveViewModel: HiveViewModel) {

    val state by hiveViewModel.state.collectAsState()
    val hive = state.selectedHive ?: return
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var showDatePicker by remember { mutableStateOf(false) }
    val inspection = state.selectedHiveInspection ?: return

    Column {
        TopAppBar(
            title = {
                Text(text = "${hive.hiveDetails.name}: ${inspection.date}")
            },
            navigationIcon = {
                IconButton(onClick = {
                    hiveViewModel.backHandler(navController)
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    hiveViewModel.onTapSettingsButton(navController)
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Hive Details")
                }
                IconButton(onClick = {
                    hiveViewModel.onTapSettingsButton(navController)
                }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    2.dp
                ),
            ),
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Date: ",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = inspection.date,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ElevatedButton(onClick = {
                        showDatePicker = !showDatePicker
                    }, contentPadding = PaddingValues(16.dp), shape = MaterialTheme.shapes.medium) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    }
                }

                val context = LocalContext.current
                if (showDatePicker) {
                    HorizontalCalendar(
                        state = calendarState,
                        dayContent = { calendarDay ->
                            val currentHiveDataEntries =
                                state.selectedHive?.hiveInspections ?: emptyList()

                            val isSelected = calendarDay.date.toString() == inspection.date

                            val hasDataEntry =
                                currentHiveDataEntries
                                    .filter { it.id != inspection.id }
                                    .any {
                                        it.date == calendarDay.date.toString()
                                    }

                            Day(day = calendarDay,
                                isSelected = isSelected,
                                hasDataEntry = hasDataEntry,
                                onClick = { day ->
                                    // update the selected data entry
                                    currentHiveDataEntries.firstOrNull { it.date == day.date.toString() }
                                        .let { existingDataEntry ->
                                            if (existingDataEntry == null) {
                                                hiveViewModel.updateSelectedInspection(
                                                    inspection.copy(
                                                        date = day.date.toString()
                                                    )
                                                )
                                                showDatePicker = false
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Data entry already exists for this date",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                })
                        },
                        monthHeader = { Month(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentPadding = PaddingValues(4.dp),
                    )
                }

                // notes
                TextField(
                    value = inspection.notes ?: "",
                    onValueChange = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                notes = it.trimStart()
                            )
                        )
                    },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 12,
                )

                DataEntryChip(title = "Odor",
                    selectedValue = inspection.hiveConditions.odor,
                    enumClass = Odor::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    odor = it
                                )
                            )
                        )
                    })

                // equipment condition
                DataEntryChip(title = "Equipment Condition",
                    selectedValue = inspection.hiveConditions.equipmentCondition,
                    enumClass = EquipmentCondition::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    equipmentCondition = it
                                )
                            )
                        )
                    })

                // frames and combs
                DataEntryChip(title = "Frames and Combs",
                    selectedValue = inspection.hiveConditions.framesAndCombs,
                    enumClass = FramesAndCombs::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    framesAndCombs = it
                                )
                            )
                        )
                    })

                // foundation type
                DataEntryChip(title = "Foundation Type",
                    selectedValue = inspection.hiveConditions.foundationType,
                    enumClass = FoundationType::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    foundationType = it
                                )
                            )
                        )
                    })

                // temperament
                DataEntryChip(title = "Temperament",
                    selectedValue = inspection.hiveConditions.temperament,
                    enumClass = Temperament::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    temperament = it
                                )
                            )
                        )
                    })

                // population
                DataEntryChip(title = "Population",
                    selectedValue = inspection.hiveConditions.population,
                    enumClass = Population::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    population = it
                                )
                            )
                        )
                    })

                // queen cells
                DataEntryChip(title = "Queen Cells",
                    selectedValue = inspection.hiveConditions.queenCells,
                    enumClass = QueenCells::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    queenCells = it
                                )
                            )
                        )
                    })

                // queen spotted
                DataEntryChip(title = "Queen Spotted",
                    stringValues = listOf("Yes", "No"),
                    selectedValue = inspection.hiveConditions.queenSpotted?.let { if (it) "Yes" else "No" },
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    queenSpotted = it == "Yes"
                                )
                            )
                        )
                    })

                // queen marker
                DataEntryChip(title = "Queen Marker",
                    selectedValue = inspection.hiveConditions.queenMarker,
                    enumClass = QueenMarker::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    queenMarker = it
                                )
                            )
                        )
                    })

                // laying pattern
                DataEntryChip(title = "Laying Pattern",
                    selectedValue = inspection.hiveConditions.layingPattern,
                    enumClass = LayingPattern::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    layingPattern = it
                                )
                            )
                        )
                    })

                // brood stage
                DataEntryChip(title = "Brood Stage",
                    selectedValue = inspection.hiveConditions.broodStage,
                    enumClass = BroodStage::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    broodStage = it
                                )
                            )
                        )
                    })

                // weather
                DataEntryChip(title = "Weather",
                    selectedValue = inspection.hiveConditions.weather,
                    enumClass = Weather::class.java,
                    onChipSelected = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    weather = it
                                )
                            )
                        )
                    })

                // temperature (text field)
                TextField(
                    value = inspection.hiveConditions.temperatureFahrenheit?.toString() ?: "",
                    onValueChange = {
                        hiveViewModel.updateSelectedInspection(
                            inspection.copy(
                                hiveConditions = inspection.hiveConditions.copy(
                                    temperatureFahrenheit = it.toDoubleOrNull()
                                )
                            )
                        )
                    },
                    label = { Text("Temperature") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1,
                    // set keyboard to number
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {
                hiveViewModel.saveInspection(navController = navController)
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Entry")
        }
    }
}