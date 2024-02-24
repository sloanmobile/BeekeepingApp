package com.reedsloan.beekeepingapp.presentation.common.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarMonth

@Composable
fun Month(month: CalendarMonth, arrowBack: () -> Unit, arrowForward: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // arrow back
        IconButton(
            onClick = {
                arrowBack()
            }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Arrow Forward Ios"
            )
        }
        Text(
            text = "${month.yearMonth.month.name} ${month.yearMonth.year}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        // arrow forward
        IconButton(
            onClick = {
                arrowForward()
            }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Arrow Forward Ios"
            )
        }
    }
}