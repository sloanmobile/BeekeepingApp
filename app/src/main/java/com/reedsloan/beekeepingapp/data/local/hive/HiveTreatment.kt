package com.reedsloan.beekeepingapp.data.local.hive

/*
TREATMENTS: CHECKMITE+, FUMAGILIM-B, TERRAMYCIN, APISTAN, MITE-A-THOL, TERR-PRO, MITE AWAY, FORMIC ACID, MITE-A-THOL, TYLAN, APIVAR, OTHER
DATE APPLIED:
DATE REMOVED:
IPM: SCREENED BOTTOM BOARD, POWDER SUGAR ROLL, ALCOHOL WASH, DRONE CELL FOUNDATION, SMALL HIVE BEETLE TRAP, OTHER

 */
data class HiveTreatment(
    val treatment: Treatment,
    val dateApplied: String,
    val dateRemoved: String,
    val ipm: List<IPM>
)
