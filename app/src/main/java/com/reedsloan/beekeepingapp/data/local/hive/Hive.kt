package com.reedsloan.beekeepingapp.data.local.hive

data class Hive(
    val id: String,
    val hiveDetails: HiveInfo,
    val hiveDataEntries: List<HiveDataEntry> = emptyList(),
    val displayOrder: Int,
)