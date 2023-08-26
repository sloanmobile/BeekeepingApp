package com.reedsloan.beekeepingapp.presentation.home_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.reedsloan.beekeepingapp.presentation.hive_info.Day
import com.reedsloan.beekeepingapp.presentation.hive_info.Month
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import java.time.YearMonth

@Composable
fun QuickLogScreen(navController: NavController, hiveViewModel: HiveViewModel) {

    val state by hiveViewModel.state.collectAsState()
    val hives by hiveViewModel.hives.collectAsState()
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek =
        remember { firstDayOfWeekFromLocale() } // Available from the library

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var showDatePicker by remember { mutableStateOf(false) }
    val entry = state.selectedDataEntry

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally, // Aligns content horizontally to the center
        verticalArrangement = Arrangement.Top, // Arranges content vertically from the top
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Text(
                text = "Record Hive Data",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            DataEntryChip(
                stringValues = hives.map { it.hiveDetails.name },
                selectedValue = state.selectedHive?.hiveDetails?.name,
                onChipSelected = { selectedHiveName ->
                    val selectedHive = hives.find { it.hiveDetails.name == selectedHiveName }
                    if (selectedHive != null) {
                        hiveViewModel.setSelectedHive(selectedHive.id)
                        hiveViewModel.setSelectedDataEntry(entry.copy(hiveId = selectedHive.id))
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Date: ",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = entry.date,
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

            Text(
                text = "Current Hive Data Entries ${ 
                    state.selectedHive?.hiveDataEntries?.map { it.date }
                }", style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            val context = LocalContext.current
            if (showDatePicker) {
                HorizontalCalendar(
                    state = calendarState,
                    dayContent = { calendarDay ->
                        val currentHiveDataEntries =
                            state.selectedHive?.hiveDataEntries ?: emptyList()

                        val hasDataEntry =
                            currentHiveDataEntries.any { it.date == calendarDay.date.toString() }

                        val isSelected = calendarDay.date.toString() == entry.date

                        Day(
                            day = calendarDay,
                            isSelected = isSelected,
                            hasDataEntry = hasDataEntry,
                            onClick = { day ->
                                // update the selected data entry
                                currentHiveDataEntries.firstOrNull { it.date == day.date.toString() }
                                    .let { existingDataEntry ->
                                        if(existingDataEntry == null) {
                                            // update the selected data entry to the default
                                            hiveViewModel.setSelectedDataEntry(
                                                hiveViewModel.getDefaultDataEntry().copy(
                                                    date = day.date.toString()
                                                )
                                            )
                                        } else {
                                            hiveViewModel.setSelectedDataEntry(existingDataEntry)
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
            DataEntryChip(
                title = "Odor",
                selectedValue = entry.hiveConditions.odor,
                enumClass = Odor::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                odor = it
                            )
                        )
                    )
                }
            )

            // equipment condition
            DataEntryChip(
                title = "Equipment Condition",
                selectedValue = entry.hiveConditions.equipmentCondition,
                enumClass = EquipmentCondition::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                equipmentCondition = it
                            )
                        )
                    )
                }
            )

            // frames and combs
            DataEntryChip(
                title = "Frames and Combs",
                selectedValue = entry.hiveConditions.framesAndCombs,
                enumClass = FramesAndCombs::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                framesAndCombs = it
                            )
                        )
                    )
                }
            )
//
//        val list by remember { mutableStateOf(listOf("1", "2", "3")) }
//        var selected: List<String> by remember { mutableStateOf(emptyList()) }
//        MultiDataEntryChip(
//            stringValues = list,
//            selectedValues = selected,
//            onChipSelected = {
//                selected = it
//            }
//        )

            // foundation type
            DataEntryChip(
                title = "Foundation Type",
                selectedValue = entry.hiveConditions.foundationType,
                enumClass = FoundationType::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                foundationType = it
                            )
                        )
                    )
                }
            )

            /*
                var odor: Odor? = null,
        val equipmentCondition: EquipmentCondition? = null,
        val hiveCondition: HiveCondition? = null,
        val framesAndCombs: FramesAndCombs? = null,
        val foundationType: FoundationType? = null,
        val temperament: Temperament? = null,
        val population: Population? = null,
        val queenCells: QueenCells? = null,
        val queenSpotted: Boolean? = null,
        val queenMarker: QueenMarker? = null,
        val layingPattern: LayingPattern? = null,
        val broodStage: BroodStage? = null,
        val weather: Weather? = null,
        val temperatureFahrenheit: Double? = null,
             */

            // temperament
            DataEntryChip(
                title = "Temperament",
                selectedValue = entry.hiveConditions.temperament,
                enumClass = Temperament::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                temperament = it
                            )
                        )
                    )
                }
            )

            // population
            DataEntryChip(
                title = "Population",
                selectedValue = entry.hiveConditions.population,
                enumClass = Population::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                population = it
                            )
                        )
                    )
                }
            )

            // queen cells
            DataEntryChip(
                title = "Queen Cells",
                selectedValue = entry.hiveConditions.queenCells,
                enumClass = QueenCells::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                queenCells = it
                            )
                        )
                    )
                }
            )

            // queen spotted
            DataEntryChip(
                title = "Queen Spotted",
                stringValues = listOf("Yes", "No"),
                selectedValue = entry.hiveConditions.queenSpotted?.let { if (it) "Yes" else "No" },
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                queenSpotted = it == "Yes"
                            )
                        )
                    )
                }
            )

            // queen marker
            DataEntryChip(
                title = "Queen Marker",
                selectedValue = entry.hiveConditions.queenMarker,
                enumClass = QueenMarker::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                queenMarker = it
                            )
                        )
                    )
                }
            )

            // laying pattern
            DataEntryChip(
                title = "Laying Pattern",
                selectedValue = entry.hiveConditions.layingPattern,
                enumClass = LayingPattern::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                layingPattern = it
                            )
                        )
                    )
                }
            )

            // brood stage
            DataEntryChip(
                title = "Brood Stage",
                selectedValue = entry.hiveConditions.broodStage,
                enumClass = BroodStage::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                broodStage = it
                            )
                        )
                    )
                }
            )

            // weather
            DataEntryChip(
                title = "Weather",
                selectedValue = entry.hiveConditions.weather,
                enumClass = Weather::class.java,
                onChipSelected = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
                                weather = it
                            )
                        )
                    )
                }
            )

            // temperature (text field)
            TextField(
                value = entry.hiveConditions.temperatureFahrenheit?.toString() ?: "",
                onValueChange = {
                    hiveViewModel.setSelectedDataEntry(
                        entry.copy(
                            hiveConditions = entry.hiveConditions.copy(
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

    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {
                hiveViewModel.onTapSaveDataEntry()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Entry")
        }
    }
}