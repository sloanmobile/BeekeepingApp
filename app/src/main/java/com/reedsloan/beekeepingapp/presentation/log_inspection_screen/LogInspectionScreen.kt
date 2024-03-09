package com.reedsloan.beekeepingapp.presentation.log_inspection_screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.reedsloan.beekeepingapp.data.local.hive.BroodStage
import com.reedsloan.beekeepingapp.data.local.hive.EquipmentCondition
import com.reedsloan.beekeepingapp.data.local.hive.FoundationType
import com.reedsloan.beekeepingapp.data.local.hive.FramesAndCombs
import com.reedsloan.beekeepingapp.data.local.hive.HiveDisease
import com.reedsloan.beekeepingapp.data.local.hive.LayingPattern
import com.reedsloan.beekeepingapp.data.local.hive.Odor
import com.reedsloan.beekeepingapp.data.local.hive.Population
import com.reedsloan.beekeepingapp.data.local.hive.QueenCells
import com.reedsloan.beekeepingapp.data.local.hive.QueenMarker
import com.reedsloan.beekeepingapp.data.local.hive.Temperament
import com.reedsloan.beekeepingapp.data.local.hive.Treatment
import com.reedsloan.beekeepingapp.data.local.hive.WeatherCondition
import com.reedsloan.beekeepingapp.data.local.hive.WindSpeed
import com.reedsloan.beekeepingapp.presentation.ads.AdViewModel
import com.reedsloan.beekeepingapp.presentation.common.DataEntryChip
import com.reedsloan.beekeepingapp.presentation.common.Divider
import com.reedsloan.beekeepingapp.presentation.common.MultiDataEntryChip
import com.reedsloan.beekeepingapp.presentation.common.calendar.Day
import com.reedsloan.beekeepingapp.presentation.common.calendar.Month
import com.reedsloan.beekeepingapp.presentation.hives_screen.HiveViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInspectionScreen(
    navController: NavController, hiveViewModel: HiveViewModel, adViewModel: AdViewModel
) {
    val state by hiveViewModel.state.collectAsState()
    val hive = state.selectedHive
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
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(key1 = lifecycle) {
        adViewModel.loadAd()
    }
    val context = LocalContext.current
    val activity = context as Activity
    val inspection = state.selectedHiveInspection

    if (inspection != null && hive != null) {
        Column {
            TopAppBar(
                title = {
                    Text(text = "${hive.hiveDetails.name}: ${inspection.date}")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        hiveViewModel.backHandler(navController)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                HiveLogSection(title = "Hive Info") {
                    HorizontalDivider()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                    )
                    {
                        Row(
                            modifier = Modifier.padding(
                                top = 8.dp,
                                start = 4.dp,
                                end = 4.dp,
                            )
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inspection Date",
                                fontSize = 20.sp,
                            )
                        }
                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            val (text, button) = createRefs()

                            Text(
                                text = inspection.date,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.constrainAs(text) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                            )

                            // reset the date to today
                            ElevatedButton(
                                onClick = {
                                    hiveViewModel.resetDateToToday()
                                },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .constrainAs(button) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(text.end)
                                    },
                                elevation = ButtonDefaults.buttonElevation(2.dp, 2.dp, 2.dp, 2.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Restore,
                                    contentDescription = "Restore"
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = "Today"
                                )
                            }
                        }

                        val coroutineScope = rememberCoroutineScope()


                        HorizontalCalendar(
                            state = calendarState,
                            dayContent = { calendarDay ->
                                val currentHiveDataEntries =
                                    state.selectedHive?.hiveInspections ?: emptyList()

                                val isSelected = calendarDay.date.toString() == inspection.date

                                val hasDataEntry =
                                    currentHiveDataEntries.filter { it.id != inspection.id }
                                        .any {
                                            it.date == calendarDay.date.toString()
                                        }

                                Day(day = calendarDay,
                                    isSelected = isSelected,
                                    hasDataEntry = hasDataEntry,
                                    onClick = { day ->
                                        // update the selected data entry
                                        hiveViewModel.updateSelectedInspection(
                                            inspection.copy(
                                                date = day.date.toString()
                                            )
                                        )
                                    })
                            },
                            monthHeader = {
                                Month(
                                    month = it,
                                    arrowBack = {
                                        coroutineScope.launch {
                                            calendarState.animateScrollToMonth(
                                                calendarState.firstVisibleMonth.yearMonth.minusMonths(
                                                    1
                                                )
                                            )
                                        }
                                    },
                                    arrowForward = {
                                        coroutineScope.launch {
                                            calendarState.animateScrollToMonth(
                                                calendarState.firstVisibleMonth.yearMonth.plusMonths(
                                                    1
                                                )
                                            )
                                        }
                                    })

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(8.dp)
                        )
                    }

                    HorizontalDivider()
                    Row(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 4.dp,
                            end = 4.dp,
                        )
                    ) {
                        Icon(Icons.Default.NoteAlt, contentDescription = "Note Alt")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Inspection Notes",
                            fontSize = 20.sp,
                        )
                    }


                    // notes
                    OutlinedTextField(
                        value = inspection.notes ?: "",
                        onValueChange = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    notes = it.trimStart()
                                )
                            )
                        },
                        label = { Text("Inspection notes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .padding(horizontal = 8.dp),
                        singleLine = false,
                        maxLines = 12,
                    )
                    HorizontalDivider()
                }

                HiveLogSection(title = "Environment") {
                    // weather
                    DataEntryChip(
                        title = "Conditions",
                        selectedValue = inspection.hiveConditions.weatherCondition,
                        enumClass = WeatherCondition::class.java,
                        onChipSelected = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    hiveConditions = inspection.hiveConditions.copy(
                                        weatherCondition = it
                                    )
                                )
                            )
                        },
                        icon = {
                            Icon(Icons.Default.Cloud, contentDescription = "Cloud")
                        },
                        // show the divider on top
                        divider = Divider(top = true, bottom = true)
                    )
                    Column {

                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            Icon(
                                Icons.Default.Thermostat, contentDescription = "Thermometer"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Temperature and Humidity", fontSize = 20.sp
                            )
                        }
                        Row(Modifier.padding(horizontal = 8.dp)) {
                            // temperature (text field)
                            OutlinedTextField(
                                value = inspection.hiveConditions.temperature?.toString()
                                    ?: "",
                                onValueChange = {
                                    hiveViewModel.onDoubleValueChange(
                                        inspection.hiveConditions.temperature?.toString()
                                            ?: "", it
                                    ) { result ->
                                        hiveViewModel.updateSelectedInspection(
                                            inspection.copy(
                                                hiveConditions = inspection.hiveConditions.copy(
                                                    temperature = result
                                                )
                                            )
                                        )
                                    }
                                },
                                label = { Text("Temperature") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                singleLine = true,
                                maxLines = 1,
                                // set keyboard to number
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // humidity (text field number)
                            OutlinedTextField(
                                value = inspection.hiveConditions.humidity?.toString() ?: "",
                                onValueChange = {
                                    hiveViewModel.onDoubleValueChange(
                                        inspection.hiveConditions.humidity?.toString() ?: "", it
                                    ) { result ->
                                        hiveViewModel.updateSelectedInspection(
                                            inspection.copy(
                                                hiveConditions = inspection.hiveConditions.copy(
                                                    humidity = result
                                                )
                                            )
                                        )
                                    }
                                },
                                label = { Text("Humidity") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                singleLine = true,
                                maxLines = 1,
                                // set keyboard to number
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            )
                        }
                    }

                    // wind amount
                    DataEntryChip(
                        title = "Wind Speed",
                        selectedValue = inspection.hiveConditions.windSpeed,
                        enumClass = WindSpeed::class.java,
                        onChipSelected = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    hiveConditions = inspection.hiveConditions.copy(
                                        windSpeed = it
                                    )
                                )
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.Air, contentDescription = "Edit Hive Details"
                            )
                        },
                        divider = Divider(top = true, bottom = true)
                    )
                    // What's blooming now
                    OutlinedTextField(
                        value = inspection.hiveConditions.bloomingNow ?: "",
                        onValueChange = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    hiveConditions = inspection.hiveConditions.copy(
                                        bloomingNow = it.trimStart()
                                    )
                                )
                            )
                        },
                        label = { Text("What's blooming now?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        singleLine = false,
                        maxLines = 4,
                    )
                    HorizontalDivider()

                }

                HiveLogSection(title = "Hive Conditions") {

                    DataEntryChip(
                        title = "Odor",
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
                        },
                        // show the divider on top
                        divider = Divider(top = true, bottom = true)
                    )

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
                    DataEntryChip(title = "Frames",
                        selectedValue = inspection.hiveConditions.frames,
                        enumClass = FramesAndCombs::class.java,
                        onChipSelected = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    hiveConditions = inspection.hiveConditions.copy(
                                        frames = it
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

                }

                // Diseases and treatments category
                HiveLogSection(title = "Hive Health") {
                    HorizontalDivider()

                    Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
                        Icon(Icons.Default.MonitorHeart, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Overall Health Estimation",
                            fontSize = 20.sp,
                        )
                    }

                    var sliderPosition by remember {
                        mutableFloatStateOf(inspection.hiveHealth.healthEstimation)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Slider(
                            value = sliderPosition, onValueChange = {
                                sliderPosition = floor(it)
                                hiveViewModel.updateHiveHealthEstimation(sliderPosition)
                            }, colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary,
                                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            ), steps = 9, valueRange = 0f..10f
                        )
                        Text(text = sliderPosition.toString())
                    }
                    // diseases (select many)
                    MultiDataEntryChip(
                        title = "Diseases (select many)",
                        enumClass = HiveDisease::class.java,
                        selectedValues = inspection.hiveHealth.diseases,
                        onChipSelected = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    hiveHealth = inspection.hiveHealth.copy(
                                        diseases = it
                                    )
                                )
                            )
                        },
                        divider = Divider(top = true, bottom = true)
                    )


                    // treatments (select many)
                    MultiDataEntryChip(
                        title = "Treatments (select many)",
                        enumClass = Treatment::class.java,
                        selectedValues = inspection.hiveTreatments,
                        onChipSelected = {
                            hiveViewModel.updateSelectedInspection(
                                inspection.copy(
                                    hiveTreatments = it
                                )
                            )
                        },
                    )
                }

                // This is a spacer to push the FAB up a bit so it doesn't cover the last entry
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }


    // Save Inspection FAB
    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {
                hiveViewModel.saveInspection(navController)
                adViewModel.showAd(activity = activity)
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Inspection")
        }
    }
}

@Composable
fun HiveLogSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 4.dp)
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}
