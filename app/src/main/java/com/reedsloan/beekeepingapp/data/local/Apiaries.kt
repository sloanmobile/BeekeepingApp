package com.reedsloan.beekeepingapp.data.local

import com.reedsloan.beekeepingapp.data.local.hive.Hive

data class Apiaries(
    val apiaryId: Int,
    val apiaryName: String,
    val apiaryLocation: String,
    val apiaryLatitude: Double,
    val apiaryLongitude: Double,
    val apiaryNotes: String,
    val apiaryHives: List<Hive>
)
