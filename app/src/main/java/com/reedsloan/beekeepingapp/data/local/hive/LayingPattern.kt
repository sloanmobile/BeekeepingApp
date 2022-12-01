package com.reedsloan.beekeepingapp.data.local.hive

//LAYING PATTERN: SPOTTY, COMPACT, TIGHT, AVERAGE, NULL
enum class LayingPattern(displayValue: String) {
    SPOTTY("Spotty"),
    COMPACT("Compact"),
    TIGHT("Tight"),
    AVERAGE("Average"),
    NULL("Null")
}