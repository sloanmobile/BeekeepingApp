package com.reedsloan.beekeepingapp.data.local.hive

data class Hive(
    val id: String,
    val hiveInfo: HiveInfo,
    val hiveDataEntries: List<HiveDataEntry> = emptyList(),
)