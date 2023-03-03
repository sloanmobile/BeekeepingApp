package com.reedsloan.beekeepingapp.presentation.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.common.Container
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.ui.theme.Typography

@Composable
fun HomeScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel
) {
    Column(
        modifier = Modifier
            .testTag("HomeScreen")
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Container {
            Text(
                style = Typography.h2, text = "Welcome to Beekeeping App.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}