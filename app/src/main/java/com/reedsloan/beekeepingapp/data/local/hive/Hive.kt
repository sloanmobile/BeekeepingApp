package com.reedsloan.beekeepingapp.data.local.hive

data class Hive(
    val hiveInfo: HiveInfo,
    val hiveConditions: HiveConditions,
    val hiveHealth: HiveHealth,
    val feeding: HiveFeeding,
    val hiveNotes: HiveNotes,
    val localPhotoUris: List<String> = emptyList(),
)
