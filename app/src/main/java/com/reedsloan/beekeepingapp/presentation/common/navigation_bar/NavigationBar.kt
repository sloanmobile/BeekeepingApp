package com.reedsloan.beekeepingapp.presentation.common.navigation_bar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.presentation.common.Container
import com.reedsloan.beekeepingapp.presentation.common.Menu
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel


@Composable
fun NavigationBar(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    val menuHeight = remember { Animatable(64.dp.value) }
    val screenName = state.currentScreenName

    val menuState = hiveViewModel.state.navigationBarMenuState

}


