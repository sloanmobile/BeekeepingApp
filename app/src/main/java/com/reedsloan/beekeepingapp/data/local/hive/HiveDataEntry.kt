package com.reedsloan.beekeepingapp.data.local.hive

import java.time.LocalDate

data class HiveDataEntry(
    val hiveId: String,
    // date is in the format of "yyyy-MM-dd" e.g. "2021-01-15"
    val date: String,
    val hiveConditions: HiveConditions,
    val hiveHealth: HiveHealth,
    val feeding: HiveFeeding,
    val localPhotoUris: List<String> = emptyList(),
)