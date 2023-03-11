package com.reedsloan.beekeepingapp.presentation.hive_info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.date_selection.DatePicker

@Composable
fun HiveInfoScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .testTag("HiveInfoScreen")
    ) {

        val state = hiveViewModel.state

        // Hive Name
        state.selectedHive?.hiveInfo?.let { hiveInfo ->
            Text(
                text = hiveInfo.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            DatePicker(
                hiveViewModel = hiveViewModel
            )

        }
    }
}