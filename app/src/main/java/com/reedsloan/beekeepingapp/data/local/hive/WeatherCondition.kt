package com.reedsloan.beekeepingapp.data.local.hive

enum class WeatherCondition(val displayValue: String) {
    UNKNOWN("Unknown"),
    CLEAR_SUNNY("Clear, Sunny"),
    MOSTLY_CLEAR("Mostly Clear"),
    PARTLY_CLOUDY("Partly Cloudy"),
    MOSTLY_CLOUDY("Mostly Cloudy"),
    CLOUDY("Cloudy"),
    FOG("Fog"),
    LIGHT_FOG("Light Fog"),
    DRIZZLE("Drizzle"),
    RAIN("Rain"),
    LIGHT_RAIN("Light Rain"),
    HEAVY_RAIN("Heavy Rain"),
    SNOW("Snow"),
    FLURRIES("Flurries"),
    LIGHT_SNOW("Light Snow"),
    HEAVY_SNOW("Heavy Snow"),
    FREEZING_DRIZZLE("Freezing Drizzle"),
    FREEZING_RAIN("Freezing Rain"),
    LIGHT_FREEZING_RAIN("Light Freezing Rain"),
    HEAVY_FREEZING_RAIN("Heavy Freezing Rain"),
    ICE_PELLETS("Ice Pellets"),
    HEAVY_ICE_PELLETS("Heavy Ice Pellets"),
    LIGHT_ICE_PELLETS("Light Ice Pellets"),
    THUNDERSTORM("Thunderstorm")
}