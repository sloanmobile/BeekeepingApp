package com.reedsloan.beekeepingapp.data.local.hive

data class HiveConditions (
    /*
    ODOR: NORMAL, FOUL
    EQUIPMENT CONDITION: GOOD, FAIR, POOR, DAMAGED
    HIVE CONDITION: BRACE COMB, EXCESS PROPOLIS
    FRAMES & COMBS: BROOD, HONEY, POLLEN, OPEN COMB
    FOUNDATION TYPE: WIRED, PLASTIC, DRONE CELL, PLASTICELL
    TEMPERAMENT: CALM, NERVOUS, AGGRESSIVE
    POPULATION: CROWDED,, MODERATE, LOW

    QUEEN CELLS: SWARM CELL, SUPERCEDURE CELLS
    QUEEN: SPOTTED
    QUEEN: MARKED, CLIPPED

    LAYING PATTERN: SPOTTY, COMPACT, TIGHT, AVERAGE, NULL
    BROOD STAGE: EGG, LARVAE, PUPA
     */
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
