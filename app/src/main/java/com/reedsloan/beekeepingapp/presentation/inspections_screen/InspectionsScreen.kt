package com.reedsloan.beekeepingapp.presentation.inspections_screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.local.hive.HiveInspection
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionsScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state by hiveViewModel.state.collectAsState()
    val hive = state.selectedHive ?: return

    Box(
        Modifier
            .fillMaxSize()
            .zIndex(1F)
    ) {
        ExtendedFloatingActionButton(
            onClick = { hiveViewModel.onTapAddInspectionButton() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "ADD INSPECTION")
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = "${hive.hiveDetails.name}: Inspections")
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
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    2.dp
                ),
            ),
        )

        InspectionsList(hiveViewModel = hiveViewModel, navController = navController)
    }
}

@Composable
fun InspectionsList(hiveViewModel: HiveViewModel, navController: NavController) {
    val state by hiveViewModel.state.collectAsState()
    val inspections = state.selectedHive?.hiveInspections ?: return
    LazyColumn {
        items(inspections.size) { index ->
            InspectionListItem(
                hiveViewModel = hiveViewModel,
                inspection = inspections[index],
                navController = navController
            )
        }
    }
}

@Composable
fun InspectionListItem(
    hiveViewModel: HiveViewModel,
    inspection: HiveInspection,
    navController: NavController,
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val pressOffset = remember { mutableStateOf(DpOffset.Zero) }

    val itemHeight by remember {
        mutableStateOf(0.dp)
    }

    val context = LocalContext.current
    val dropdownInteractionSource = remember { MutableInteractionSource() }

    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { hiveViewModel.onTapInspectionButton(inspection, navController) }) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier
                    .zIndex(3F)
                    .padding(8.dp)
                    .indication(
                        interactionSource = dropdownInteractionSource,
                        indication = rememberRipple()
                    )
                    .pointerInput(true) {
                        detectTapGestures(
                            onPress = { offset ->
                                pressOffset.value = DpOffset(offset.x.toDp(), offset.y.toDp())
                                isContextMenuVisible = true
                                val press = PressInteraction.Press(offset)
                                dropdownInteractionSource.emit(press)
                                awaitRelease()
                                dropdownInteractionSource.emit(PressInteraction.Release(press))
                            },
                        )
                    }
                ) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    DropdownMenu(expanded = isContextMenuVisible,
                        offset = pressOffset.value,
                        onDismissRequest = {
                            isContextMenuVisible = false
                        }) {
                        DropdownMenuItem(onClick = {
                            hiveViewModel.onTapDeleteInspectionButton(inspection)
                            isContextMenuVisible = false
                        }, text = {
                            Text(text = "Delete")
                        }, leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        })

                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Text(text = inspection.date, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = inspection.notes,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
        }
    }
}
