package com.reedsloan.beekeepingapp.data.local.hive

data class HiveInspection(
    val hiveId: String,
    // date is in the format of "yyyy-MM-dd" e.g. "2021-01-15"
    val date: String,
    val hiveConditions: HiveConditions,
    val hiveHealth: HiveHealth,
    val feeding: HiveFeeding,
    val localPhotoUris: List<String> = emptyList(),
    val notes: String = "No notes added."
)