package com.reedsloan.beekeepingapp

import com.reedsloan.beekeepingapp.presentation.hives_screen.EditHiveMenu
import com.reedsloan.beekeepingapp.presentation.hives_screen.HivesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reedsloan.beekeepingapp.presentation.common.containers.SideSheetContainer
import com.reedsloan.beekeepingapp.presentation.hive_info.HiveInfoScreen
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeScreen
import com.reedsloan.beekeepingapp.presentation.home_screen.WorkInProgressOverlayText
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import com.reedsloan.beekeepingapp.presentation.settings.SettingsScreen
import com.reedsloan.beekeepingapp.presentation.ui.theme.AppTheme
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val hiveViewModel = hiltViewModel<HiveViewModel>()
            // A surface container using the 'background' color from the theme
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isDebugBuild by remember { mutableStateOf(BuildConfig.DEBUG) }

                    if (isDebugBuild) {
                        WorkInProgressOverlayText()
                    }

                    val navController = rememberNavController()
                    val state = hiveViewModel.state

                    state.selectedHive?.let { hive ->
                        SideSheetContainer(
                            // using a raw string with """ instead of " to get quotes in the string
                            display = state.editHiveMenuState.isOpen(),
                            title = """Editing "${hive.hiveInfo.name}"""",
                            onDismiss = { hiveViewModel.onDismissEditHiveMenu() }) {
                            EditHiveMenu(
                                hiveViewModel = hiveViewModel,
                                hive = hive,
                                navController = navController,
                            )
                        }
                    }

                    // replace with custom bottom bar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1F),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        BottomBar(navController)
                    }

                    Column(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    hiveViewModel.onTapOutside()
                                })
                            }) {

                        BackHandler {
                            hiveViewModel.backHandler(navController)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp)
                        ) {
                            // fragment for each screen
                            NavHost(
                                navController = navController,
                                startDestination = Screen.HomeScreen.route,
                            ) {
                                // home screen
                                composable(
                                    route = Screen.HomeScreen.route
                                ) {
                                    HomeScreen(navController, hiveViewModel)
                                }
                                // add hive screen
                                composable(
                                    route = Screen.HiveScreen.route
                                ) {
                                    HivesScreen(navController, hiveViewModel)
                                }
                                // hive info screen
                                composable(
                                    route = Screen.HiveInfoScreen.route,
                                ) {
                                    HiveInfoScreen(navController, hiveViewModel)
                                }
                                // settings screen
                                composable(
                                    route = Screen.SettingsScreen.route
                                ) {
                                    SettingsScreen(navController, hiveViewModel)
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
fun BottomBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Hives", "Settings")

    val icons = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.Hive,
        Icons.Rounded.Settings
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1F)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        selectedItem = index
                        // navigate
                        when (index) {
                            0 -> navController.navigate(Screen.HomeScreen.route)
                            1 -> navController.navigate(Screen.HiveScreen.route)
                            2 -> navController.navigate(Screen.SettingsScreen.route)
                        }
                    },
            ) {
                Button(
                    onClick = {
                        selectedItem = index
                        // navigate
                        when (index) {
                            0 -> navController.navigate(Screen.HomeScreen.route)
                            1 -> navController.navigate(Screen.HiveScreen.route)
                            2 -> navController.navigate(Screen.SettingsScreen.route)
                        }
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (selectedItem == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(
                            2.dp
                        ),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(0.dp),
                        tint = if (selectedItem == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(text = item, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}