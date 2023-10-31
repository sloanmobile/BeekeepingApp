package com.reedsloan.beekeepingapp.presentation.common.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay

@Composable
fun Day(
    day: CalendarDay,
    hasDataEntry: Boolean,
    isSelected: Boolean,
    onClick: (CalendarDay) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f) // this is necessary for the buttons evenly to fit in the layout
        , contentAlignment = Alignment.Center
    ) {

        // Show a primary color normal button if highlighted else show a surface color elevated button
        if (isSelected) {
            // highlighted days are normal buttons
            OutlinedButton(
                onClick = { onClick(day) },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp), // this is necessary for the text to fit
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (hasDataEntry) {
            // non-highlighted days are elevated buttons
            FilledTonalButton(
                onClick = { onClick(day) },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp) // this is necessary for the text to fit
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // non-highlighted days are elevated buttons
            ElevatedButton(
                onClick = { onClick(day) },
                modifier = Modifier.size(48.dp),
                elevation = ButtonDefaults.buttonElevation(2.dp, 2.dp, 2.dp, 2.dp),
                contentPadding = PaddingValues(0.dp) // this is necessary for the text to fit
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
