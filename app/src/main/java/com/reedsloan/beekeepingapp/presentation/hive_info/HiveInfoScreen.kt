package com.reedsloan.beekeepingapp.presentation.hive_info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.local.CheckboxSelectionValues
import com.reedsloan.beekeepingapp.data.local.hive.Treatment
import com.reedsloan.beekeepingapp.presentation.common.CustomButton
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar
import com.reedsloan.beekeepingapp.presentation.common.SelectionCheckboxMenu

@Composable
fun HiveInfoScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    Column(Modifier.fillMaxSize().testTag("HiveInfoScreen")) {
        NavigationBar(navController, hiveViewModel)
        val maxWidth = with(LocalDensity.current) {
            LocalContext.current.resources.displayMetrics.widthPixels.toDp() - 16.dp * 2
        }

        Text(
            text = "Hive Info Screen",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        CustomButton(text = "Back", onClick = {
            // navigate to the previous screen
            navController.popBackStack()
        })

        SelectionCheckboxMenu(
            title = "Treatment",
            options = Treatment.values().map { it.displayValue },
            modifier = Modifier,
            dropdownWidth = maxWidth,
            checkboxSelectionValues = CheckboxSelectionValues.Builder()
                .setMaxSelectionCount(6)
                .setShowSelectionInstructions(true)
                .setDisabledValues(listOf(Treatment.CHECKMITE_PLUS.displayValue))
                .setMinSelectionCount(2)
                .setAllowCustomValues(true)
                .build(),
            hiveViewModel = hiveViewModel,
            onSubmit = { /*TODO*/ },
        )

    }
}
