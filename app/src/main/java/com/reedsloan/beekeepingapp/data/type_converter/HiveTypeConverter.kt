package com.reedsloan.beekeepingapp.data.type_converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.reedsloan.beekeepingapp.data.local.hive.Hive

class HiveTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toJson(hive: Hive): String {
        return gson.toJson(hive)
    }

    @TypeConverter
    fun toHive(json: String): Hive {
        return gson.fromJson(json, Hive::class.java)
    }
}