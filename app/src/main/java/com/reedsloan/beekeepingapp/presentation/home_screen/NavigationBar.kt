package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import com.reedsloan.beekeepingapp.presentation.ui.theme.Typography


@Composable
fun NavigationBar(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    val menuHeight = remember { Animatable(64.dp.value) }
    val screenName = state.currentScreenName

    val menuState = hiveViewModel.state.navigationBarMenuState

    // animate the menuHeight when the menuState changes
    LaunchedEffect(key1 = menuState) {
        if (menuState == MenuState.OPEN) {
            // expand the menuHeight to 192.dp
            menuHeight.animateTo(
                targetValue = 224.dp.value, animationSpec = tween(durationMillis = 300)
            )
        } else if (menuState == MenuState.CLOSED) {
            // collapse the menuHeight to 64.dp
            menuHeight.animateTo(
                targetValue = 64.dp.value, animationSpec = tween(durationMillis = 300)
            )
        }
    }

    Container {
        Column(
            modifier = Modifier
                .testTag("Navbar")
                .padding(top = 16.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .height(menuHeight.value.dp)
                .background(
                    // gradient from secondary color to onPrimaryColor (top to bottom)
                    Brush.verticalGradient(
                        colors = listOf(
                            customTheme.secondaryColor, customTheme.onPrimaryColor
                        )
                    )
                )
                .clickable {
                    hiveViewModel.onTapNavigationExpandButton()
                },
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
            ) {
                val (menuButton, title, loading) = createRefs()
                if(menuState == MenuState.CLOSED) {
                    Icon(
                        painter = painterResource(id = R.drawable.hamburger),
                        contentDescription = "Menu", tint = customTheme.primaryColor,
                        modifier = Modifier
                            .constrainAs(menuButton) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(42.dp)
                            .padding(start = 16.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = "Menu", tint = customTheme.primaryColor,
                        modifier = Modifier
                            .constrainAs(menuButton) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(42.dp)
                            .padding(start = 16.dp),
                    )
                }

                // screen title
                Text(text = screenName,
                    style = Typography.h1,
                    color = customTheme.onSecondaryText,
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp)
                        .constrainAs(title) {
                            start.linkTo(menuButton.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        })

                // beehive icon from the drawable folder
                Icon(painter = painterResource(id = R.drawable.beehive),
                    contentDescription = "Beehive",
                    tint = customTheme.onSecondaryColor,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(42.dp)
                        .constrainAs(loading) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        })
            }
            Menu(navController, hiveViewModel)
        }
    }
}


