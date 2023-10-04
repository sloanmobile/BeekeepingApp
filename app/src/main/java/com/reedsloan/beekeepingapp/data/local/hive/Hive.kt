package com.reedsloan.beekeepingapp.data.local.hive

data class Hive(
    val id: String,
    val hiveDetails: HiveInfo,
    val hiveInspections: List<HiveInspection> = emptyList(),
    val displayOrder: Int,
)