package com.reedsloan.beekeepingapp.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.viewmodel.HiveViewModel

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val hives by hiveViewModel.hives.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(text = "Hives")
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

            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(1), modifier = Modifier.fillMaxWidth()) {
                items(hives) { hive ->
                    HiveCard(
                        hive = hive,
                        navController = navController,
                        hiveViewModel = hiveViewModel
                    )
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = { hiveViewModel.onTapAddHiveButton() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .zIndex(1F)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "ADD HIVE")
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        onDismissRequest = { onDismiss() }, title = {
            Text(text = "Delete hive?")
        }, text = {
            Text(text = "Are you sure you want to delete this hive?")
        }, confirmButton = {
            Button(onClick = { onClick() }) {
                Text(text = "Delete")
            }
        }, dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        })
}

@Composable
fun HiveCard(
    hive: Hive,
    navController: NavController,
    hiveViewModel: HiveViewModel,
) {
    ElevatedCard(
        Modifier
            .padding(8.dp)
            .clickable {
                hiveViewModel.onTapHiveCard(hive.id, navController)
            }) {
        Column(
            Modifier
                .fillMaxWidth()) {
            hive.hiveDetails.image?.let { image ->
                // Hive image
                AsyncImage(
                    model = image,
                    contentDescription = "Hive image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(152.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop,
                    onError = {
                        Log.e(
                            this::class.java.simpleName,
                            "Error loading image: ${it.result.throwable}"
                        )
                    },
                    filterQuality = FilterQuality.High,
                )
            } ?: run {
                // Placeholder camera icon for when the user has not selected an image.
                Image(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(152.dp)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                        .padding(16.dp)
                        .alpha(0.5f),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.padding(16.dp)) {
                    Row {
                        // The name of the hive
                        Text(
                            text = hive.hiveDetails.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // The last inspection date of the hive
                        Text(
                            text = "Last Inspection: ${hive.hiveInspections.lastOrNull()?.date ?: "Never"}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        )
                    }
                }
            }
        }
    }
}