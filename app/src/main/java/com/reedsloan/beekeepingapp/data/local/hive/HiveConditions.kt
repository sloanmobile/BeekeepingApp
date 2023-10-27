package com.reedsloan.beekeepingapp.data.local.hive


data class HiveConditions (
    var odor: Odor? = null,
    val equipmentCondition: EquipmentCondition? = null,
    val hiveCondition: HiveCondition? = null,
    val frames: FramesAndCombs? = null,
    val foundationType: FoundationType? = null,
    val temperament: Temperament? = null,
    val population: Population? = null,
    val queenCells: QueenCells? = null,
    val queenSpotted: Boolean? = null,
    val queenMarker: QueenMarker? = null,
    val layingPattern: LayingPattern? = null,
    val broodStage: BroodStage? = null,
    val weatherCondition: WeatherCondition? = null,
    val humidity: Double? = null,
    val windSpeed: WindSpeed? = null,
    val temperatureFahrenheit: Double? = null,
)
