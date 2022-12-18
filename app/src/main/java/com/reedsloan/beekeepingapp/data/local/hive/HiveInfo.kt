package com.reedsloan.beekeepingapp.data.local.hive

data class HiveInfo(
    val id: String,
    val name: String,
    val location: String? = null,
    val notes: String? = null,
    val image: String? = null,
    val dateCreated: String = System.currentTimeMillis().toString(),
    val dateModified: String = System.currentTimeMillis().toString(),
    val dateDeleted: String? = null,
    val isDeleted: Boolean = false,
    val isModified: Boolean = false,
    val timestamp: String = System.currentTimeMillis().toString(),
    val weather: Weather? = null,
    val temperatureFahrenheit: Double? = null,
)
