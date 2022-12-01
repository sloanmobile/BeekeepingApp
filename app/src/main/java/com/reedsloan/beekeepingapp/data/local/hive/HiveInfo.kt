package com.reedsloan.beekeepingapp.data.local.hive

data class HiveInfo(
    val id: Int,
    val name: String,
    val location: String,
    val notes: String,
    val image: String,
    val dateCreated: String,
    val dateModified: String,
    val dateDeleted: String,
    val isDeleted: Boolean,
    val isModified: Boolean,
    val timestamp: String,
    val weather: Weather,
    val temperatureFahrenheit: Int,
)
