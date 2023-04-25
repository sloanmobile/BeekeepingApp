package com.reedsloan.beekeepingapp.data.local.hive

import java.time.LocalDate

data class HiveInfo(
    val name: String,
    val location: String? = null,
    val notes: String = "Lorem ipsum dolor sit amet consectetur adipisicing elit. Quisquam, quod. " +
            "Lorem ipsum dolor sit amet consectetur adipisicing elit. Quisquam, quod. ",
    val image: String? = null,
    val dateCreated: LocalDate = LocalDate.now(),
    val dateModified: LocalDate = LocalDate.now(),
    val isDeleted: Boolean = false,
)
