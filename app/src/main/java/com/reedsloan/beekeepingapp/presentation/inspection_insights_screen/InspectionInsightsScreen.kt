package com.reedsloan.beekeepingapp.presentation.inspection_insights_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.piechart.utils.toLegacyInt
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.copy
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDate

@Composable
fun InspectionInsightsScreen(
    state: InspectionInsightsScreenState,
    onEvent: (InspectionInsightsEvent) -> Unit,
) {
    val startTime by remember { mutableStateOf(LocalDate.now().minusDays(7)) }
    val endTime by remember { mutableStateOf(LocalDate.now()) }

    val humidityValues = state.inspections.mapNotNull { it.hiveConditions.humidity?.toFloat() }
    val temperatureValues =
        state.inspections.mapNotNull { it.hiveConditions.temperatureFahrenheit?.toFloat() }
    val dates = state.inspections.map { it.date }
    val healthEstimation = state.inspections.map { it.hiveHealth.healthEstimation }


    val chartEntryModel = entryModelOf(
        entriesOf(*humidityValues.toTypedArray()),
        entriesOf(*temperatureValues.toTypedArray()),
        entriesOf(*healthEstimation.toTypedArray()),
    )
    val legendItems = remember { mutableListOf("Humidity", "Temperature", "Hive Health Estimation") }
    val color1 by remember { mutableStateOf(Color(0xffb983ff)) }
    val color2 by remember { mutableStateOf(Color(0xff91b1fd)) }
    val color3 by remember { mutableStateOf(Color(0xff8fdaff)) }
    val chartColors by remember { mutableStateOf(listOf(color1, color2, color3)) }

    val defaultLines = currentChartStyle.lineChart.lines.mapIndexed { index, lineSpec ->
        lineSpec.copy(lineColor = chartColors.getOrElse(index) { color1 }.toLegacyInt(), lineBackgroundShader = null)
    }

    Chart(
        chart = lineChart(lines = defaultLines),
        model = chartEntryModel,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        marker = rememberMarker(),
        isZoomEnabled = true,
        legend = rememberLegend(legendItems)
    )
}