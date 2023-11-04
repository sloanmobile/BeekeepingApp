package com.reedsloan.beekeepingapp.presentation.hives_screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.reedsloan.beekeepingapp.core.util.TestTags
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.ContextMenuItem
import com.reedsloan.beekeepingapp.presentation.sign_in.SignInViewModel

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HivesScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel,
    signInViewModel: SignInViewModel
) {
    val hives by hiveViewModel.hives.collectAsState()
    val state by hiveViewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
        ) {
            var isContextMenuVisible by rememberSaveable {
                mutableStateOf(false)
            }

            val pressOffset by remember { mutableStateOf(DpOffset.Zero) }


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
                    IconButton(onClick = {
                        isContextMenuVisible = true
                    }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = isContextMenuVisible,
                        offset = pressOffset,
                        onDismissRequest = {
                            isContextMenuVisible = false
                        }) {
                        DropdownMenuItem(onClick = {
                            isContextMenuVisible = false
                            signInViewModel.signOut(navController)
                        }, text = {
                            Text(text = "Sign out")
                        }, leadingIcon = {
                            Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                        })
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        2.dp
                    ),
                ),
            )

            if (hives.isEmpty() && state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.LAZY_VERTICAL_GRID)
                ) {
                    items(
                        items = hives,
                        key = { hive -> hive.id }
                    ) { hive ->
                        HiveCard(
                            hive = hive,
                            navController = navController,
                            hiveViewModel = hiveViewModel
                        )
                    }
                }
            }
        }
    }
    // Delete hive dialog
    Box(Modifier.fillMaxSize()) {
        if (state.showDeleteHiveDialog) DeleteConfirmationDialog(onDismiss = { hiveViewModel.dismissDeleteHiveDialog() },
            onClick = {
                hiveViewModel.onTapDeleteHiveConfirmationButton(state.selectedHive!!.id)
                hiveViewModel.dismissDeleteHiveDialog()
            })
    }
    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = { hiveViewModel.onTapAddHiveButton() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .zIndex(1F)
                .testTag(TestTags.ADD_HIVE_BUTTON)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "ADD HIVE")
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(icon = {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }, onDismissRequest = { onDismiss() }, title = {
        Text(text = "Delete hive?")
    }, text = {
        Text(text = "Are you sure you want to delete this hive?")
    }, confirmButton = {
        Button(
            onClick = { onClick() },
            modifier = Modifier.testTag(TestTags.DELETE_HIVE_CONFIRMATION_BUTTON)
        ) {
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
    val state by hiveViewModel.state.collectAsState()
    ElevatedCard(
        Modifier
            .testTag(TestTags.HIVE_CARD)
            .padding(8.dp)
            .clickable {
                hiveViewModel.onTapHiveCard(hive.id, navController)
            }) {
        Column(
            Modifier.fillMaxWidth()
        ) {
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(start = 16.dp, bottom = 16.dp, top = 16.dp)) {
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
                var isContextMenuVisible by remember { mutableStateOf(false) }
                var pressOffset by remember { mutableStateOf(DpOffset.Zero) }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                ) {
                    ContextMenuButton(
                        isContextMenuVisible = isContextMenuVisible,
                        contextMenuItems = listOf(
                            ContextMenuItem(
                                title = "Delete",
                                icon = Icons.Filled.Delete,
                                action = {
                                    hiveViewModel.onTapDeleteHiveButton(hive)
                                }
                            )
                        ),
                        pressOffset = pressOffset,
                        onDismissRequest = {
                            isContextMenuVisible = false
                        },
                    ) {
                        hiveViewModel.setSelectedHive(hive.id)
                        pressOffset = it
                        isContextMenuVisible = true
                    }
                }
            }
        }
    }
}

@Composable
fun ContextMenuButton(
    isContextMenuVisible: Boolean,
    contextMenuItems: List<ContextMenuItem>,
    pressOffset: DpOffset = DpOffset.Zero,
    onDismissRequest: () -> Unit,
    onPress: (DpOffset) -> Unit,
    ) {
    val dropdownInteractionSource = remember { MutableInteractionSource() }

        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .padding(16.dp)
            .size(40.dp)
            // clip to circle
            .clip(CircleShape)
            .indication(
                interactionSource = dropdownInteractionSource, indication = rememberRipple()
            )
            .testTag(TestTags.CONTEXT_MENU_BUTTON)
            .pointerInput(true) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)

                        onPress(DpOffset(offset.x.toDp(), offset.y.toDp()-28.dp))
                        Log.d("ContextMenuButton", "Offset: $offset")

                        dropdownInteractionSource.emit(press)
                        tryAwaitRelease()
                        dropdownInteractionSource.emit(PressInteraction.Release(press))
                    },
                )
            }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "More")
        }

        DropdownMenu(expanded = isContextMenuVisible,
            offset = pressOffset,
            onDismissRequest = {
                onDismissRequest()
            }) {
            contextMenuItems.forEachIndexed { index, contextMenuItem ->
                DropdownMenuItem(onClick = {
                    contextMenuItem.action()
                }, text = {
                    Text(text = contextMenuItem.title)
                }, leadingIcon = {
                    contextMenuItem.icon?.let { icon ->
                        Icon(icon, contentDescription = contextMenuItem.title)
                    }
                },
                    modifier = Modifier.testTag(TestTags.CONTEXT_MENU_ITEM)
                )
            }
    }
}
