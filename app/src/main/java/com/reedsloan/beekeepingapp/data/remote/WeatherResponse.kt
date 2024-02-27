package com.reedsloan.beekeepingapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val current: Current,
    val location: Location
)