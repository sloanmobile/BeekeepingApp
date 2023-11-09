package com.reedsloan.beekeepingapp.presentation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.presentation.hives_screen.HiveViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HiveDetailsScreen(navController: NavController, hiveViewModel: HiveViewModel, setSheetOpenState: (Boolean) -> Unit, isSheetOpen: Boolean) {
    val state by hiveViewModel.state.collectAsState()
    val hive = state.selectedHive ?: return

    BackHandler {
        if (isSheetOpen) {
            setSheetOpenState(false)
        } else {
            hiveViewModel.backHandler(navController)
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = hive.hiveDetails.name)
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
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            item(key = state.isLoading, contentType = HiveScreenState::class) {
                // Photo of hive if it exists otherwise show an add photo button
                hive.hiveDetails.image?.let { image ->
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        // Hive image
                        AsyncImage(
                            model = image,
                            contentDescription = "Hive image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    setSheetOpenState(true)
                                }
                                .clip(
                                    RoundedCornerShape(
                                        bottomEndPercent = 20, bottomStartPercent = 20
                                    )
                                ),
                            contentScale = ContentScale.Crop,
                            onError = {
                                Log.e(
                                    this::class.java.simpleName,
                                    "Error loading image: ${it.result.throwable}"
                                )
                            },
                            filterQuality = FilterQuality.High,
                        )
                    }
                } ?: run {
                    // Open the bottom sheet to take a photo or select from gallery
                    Column {
                        // Add photo
                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    setSheetOpenState(true)
                                }
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                    RoundedCornerShape(
                                        bottomEndPercent = 35, bottomStartPercent = 35
                                    )
                                )
                                .clip(
                                    RoundedCornerShape(
                                        bottomEndPercent = 35, bottomStartPercent = 35
                                    )
                                )) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Photo",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Inspections action
                HiveDetailsAction(title = "Inspections",
                    description = "View and edit inspections",
                    icon = Icons.Rounded.Hive,
                    onClick = {
                        hiveViewModel.onTapInspectionsButton(navController)
                    })

                Spacer(modifier = Modifier.height(16.dp))

                // Inspections Insights action
                HiveDetailsAction(title = "Inspections Insights",
                    description = "View insights about inspections",
                    icon = Icons.Rounded.BarChart,
                    onClick = {
                        hiveViewModel.onTapInspectionsInsightsButton(navController)
                    })

                Spacer(modifier = Modifier.height(16.dp))
                // Tasks action
                HiveDetailsAction(title = "Tasks",
                    description = "View and edit tasks",
                    icon = Icons.Rounded.TaskAlt,
                    onClick = {
                        hiveViewModel.onTapTasksButton(navController)
                    })

                Spacer(modifier = Modifier.height(16.dp))
                // Manage honey action
                HiveDetailsAction(
                    title = "Manage Honey",
                    description = "View and edit honey",
                    icon = R.drawable.ic_honey,
                    onClick = {
                        hiveViewModel.onTapManageHoneyButton(navController)
                    })

                Spacer(modifier = Modifier.height(16.dp))
                // Export to CSV action
                HiveDetailsAction(title = "Export to CSV",
                    description = "Export hive data to CSV",
                    icon = Icons.Default.IosShare,
                    onClick = {
                        hiveViewModel.onTapExportToCsvButton()
                    })


                // Push the content up so it's not all the way at the bottom of the screen
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveDetailsAction(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(112.dp)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                        .copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiveDetailsAction(title: String, description: String, icon: Int, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height(112.dp)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                        .copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}