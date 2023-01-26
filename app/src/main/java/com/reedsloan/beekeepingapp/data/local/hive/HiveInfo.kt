package com.reedsloan.beekeepingapp.data.local.hive

data class HiveInfo(
    val name: String,
    val location: String? = null,
    val notes: String? = null,
    val image: String? = null,
    val dateCreated: String = System.currentTimeMillis().toString(),
    val dateModified: String = System.currentTimeMillis().toString(),
    val isDeleted: Boolean = false,
)
