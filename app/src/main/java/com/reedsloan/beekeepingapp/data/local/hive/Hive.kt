package com.reedsloan.beekeepingapp.data.local.hive

data class Hive(
    val hiveInfo: HiveInfo,
    val hiveDataEntries: List<HiveDataEntry> = emptyList(),
)