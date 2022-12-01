package com.reedsloan.beekeepingapp.data.local.hive

class HiveConditions (
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
    val odor: Odor,
    val equipmentCondition: EquipmentCondition,
    val hiveCondition: HiveCondition,
    val framesAndCombs: FramesAndCombs,
    val foundationType: FoundationType,
    val temperament: Temperament,
    val population: Population,
    val queenCells: QueenCells,
    val queenSpotted: Boolean,
    val queenMarker: QueenMarker,
    val layingPattern: LayingPattern,
    val broodStage: BroodStage,
)
