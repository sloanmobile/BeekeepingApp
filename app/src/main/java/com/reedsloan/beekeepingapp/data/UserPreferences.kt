package com.reedsloan.beekeepingapp.data

import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement

data class UserPreferences(
    val temperatureMeasurement: TemperatureMeasurement = TemperatureMeasurement.FAHRENHEIT,
    val timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
)
