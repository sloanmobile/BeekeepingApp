package com.reedsloan.beekeepingapp.data.local.hive

data class HiveDataEntry(
    val timestamp: String = System.currentTimeMillis().toString(),
    val hiveConditions: HiveConditions,
    val hiveHealth: HiveHealth,
    val feeding: HiveFeeding,
    val localPhotoUris: List<String> = emptyList(),
)