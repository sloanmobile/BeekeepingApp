package com.reedsloan.beekeepingapp.presentation.tasks_screen

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.reedsloan.beekeepingapp.core.util.TestTags
import com.reedsloan.beekeepingapp.data.local.tasks.Task
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(state: TasksScreenState, onEvent: (TasksScreenEvent) -> Unit) {
    val categories = state.userData.tasks.map { it.category }.distinct()
    val tasksInCategory = state.userData.tasks.groupBy { it.category }
    val completedTasksInCategory =
        tasksInCategory.mapValues { it.value.filter { t -> t.isCompleted } }

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .zIndex(1F)
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    onEvent(
                        TasksScreenEvent.OnCreateNewTaskClicked(
                            Task(
                                UUID.randomUUID(),
                                "My Task",
                                LocalDate.now().toString(),
                                false,
                                "Complete this task",
                                false,
                                "My Category"
                            )
                        )
                    )
                }, modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "ADD TASK")
            }
        }

        Column(Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Text(text = "Tasks")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(TasksScreenEvent.OnBackClicked)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onEvent(TasksScreenEvent.OnSettingsClicked)
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

            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LazyRow {
                    item {
                        if (state.tasksFilter != TasksFilter.AllTasks) {
                            // assist chip to clear filter
                            AssistChip(onClick = { onEvent(TasksScreenEvent.ClearTasksFilter) },
                                modifier = Modifier.padding(end = 4.dp),
                                label = {
                                    Icon(
                                        imageVector = Icons.Filled.Close, contentDescription = null
                                    )
                                })
                        }
                        categories.forEach { category ->
                            FilterChip(
                                label = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        ),
                                    )
                                },
                                onClick = { onEvent(TasksScreenEvent.OnCategoryClicked(category)) },
                                modifier = Modifier.padding(start = 8.dp),
                                selected = state.tasksFilter == TasksFilter.Category(category)
                            )
                        }
                    }
                }

                val tasksToDisplay = when (state.tasksFilter) {
                    is TasksFilter.AllTasks -> state.userData.tasks

                    is TasksFilter.CompletedTasks -> state.userData.tasks.filter { it.isCompleted }

                    is TasksFilter.IncompleteTasks -> state.userData.tasks.filter { !it.isCompleted }

                    is TasksFilter.Category -> state.userData.tasks.filter { it.category == state.tasksFilter.category }
                }.groupBy { it.category }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.LAZY_VERTICAL_GRID),
                ) {
                    item {
                        Column {
                            var expanded by remember { mutableStateOf(false) }

                            tasksToDisplay.forEach { (category, tasks) ->
                                ElevatedCard {
                                    val count = tasksInCategory[category]?.size ?: 0
                                    val progress = completedTasksInCategory[category]?.size ?: 0
                                    val total = tasksInCategory[category]?.size ?: 0
                                    val progressBar = if (total == 0) {
                                        0F
                                    } else {
                                        progress.toFloat() / total.toFloat()
                                    }

                                    Column(
                                        Modifier
                                            .clip(MaterialTheme.shapes.medium)
                                            .clickable { expanded = !expanded }
                                    ) {
                                        Column(
                                            Modifier
                                                .padding(
                                                    top = 16.dp,
                                                    start = 16.dp,
                                                    end = 16.dp
                                                )
                                        ) {
                                            Text(
                                                text = "$count tasks",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                            )
                                            TaskCategoryHeader(
                                                category = category,
                                                onEvent = onEvent
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            LinearProgressIndicator(
                                                progress = { progressBar },
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Icon(
                                                imageVector =
                                                if (expanded) Icons.Filled.KeyboardArrowUp
                                                else Icons.Filled.KeyboardArrowDown,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                            )
                                        }
                                    }


                                    // column that will expand and collapse with animation
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize()
                                    ) {
                                        if (expanded) {
                                            tasks.forEach { task ->
                                                ElevatedCard(
                                                    modifier = Modifier.padding(
                                                        vertical = 4.dp,
                                                        horizontal = 8.dp
                                                    ),
                                                ) {
                                                    TaskItem(task = task) {
                                                        onEvent(TasksScreenEvent.OnTaskClicked(task))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onClick: (Task) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        onClick(task)
    }) {
        Checkbox(checked = task.isCompleted, onCheckedChange = {
            onClick(task)
        })
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                // description
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = task.date,
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TaskCategoryHeader(category: String, onEvent: (TasksScreenEvent) -> Unit) {
    Row {
        Text(
            text = category,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
