package com.reedsloan.beekeepingapp.presentation.home_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.tasks.Task.Companion.isToday
import com.reedsloan.beekeepingapp.presentation.sign_in.SignInViewModel
import com.reedsloan.beekeepingapp.presentation.tasks_screen.TaskItem
import java.time.LocalDate
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit,
    signInViewModel: SignInViewModel,
    navController: NavController
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = "Home")
            },
            navigationIcon = {
                IconButton(onClick = {
                    onEvent(HomeScreenEvent.OnBackClicked)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    onEvent(HomeScreenEvent.OnSettingsClicked)
                }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = {
                    isContextMenuVisible = true
                }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
                DropdownMenu(expanded = isContextMenuVisible,
                    offset = DpOffset.Zero,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    }) {
                    DropdownMenuItem(onClick = {
                        isContextMenuVisible = false
                        signInViewModel.signOut(navController)
                    }, text = {
                        Text(text = "Sign out")
                    }, leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out")
                    })
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    2.dp
                ),
            )
        )

        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Today's Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    onEvent(HomeScreenEvent.OnAllTasksClicked)
                }) {

                    Text(
                        text = "All Tasks", style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        ), modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "All Tasks",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val tasksToday = state.userData.tasks.filter { it.isToday() }
            Text(
                text = "You have ${tasksToday.count()} ${if (tasksToday.size > 1) "tasks" else "task"} today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(256.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    state.userData.tasks.filter { it.isToday() }.forEach { task ->
                        TaskItem(task = task) {
                            onEvent(HomeScreenEvent.OnTaskClicked(it))
                        }
                    }
                }
            }
            // go to hives screen
            TextButton(onClick = {
                onEvent(HomeScreenEvent.OnAllHivesClicked)
            }) {
                Text(
                    text = "All Hives", style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ), modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "All Hives",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun ShortHiveCard(hive: Hive, onEvent: (HomeScreenEvent) -> Unit) {
    Column {
        OutlinedCard(modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onEvent(HomeScreenEvent.OnHiveClicked(hive))
            }) {
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = hive.hiveDetails.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val lastInspection = hive.hiveInspections.lastOrNull()
                        // how many days ago was this
                        val daysAgo = if (lastInspection == null) null else (LocalDate.now()
                            .toEpochDay() - LocalDate.parse(lastInspection.date)
                            .toEpochDay()).days.inWholeDays
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Recent Inspection",
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when {
                                daysAgo == null -> "No Inspections"
                                daysAgo < -1L -> "${-daysAgo} days from now"
                                daysAgo == -1L -> "Tomorrow"
                                daysAgo == 0L -> "Today"
                                daysAgo == 1L -> "Yesterday"
                                else -> "$daysAgo days ago"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                hive.hiveDetails.image?.let { image ->

                    val hiveImage = rememberAsyncImagePainter(model = image)

                    Image(
                        painter = hiveImage,
                        contentDescription = "Hive Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        }
    }
}

