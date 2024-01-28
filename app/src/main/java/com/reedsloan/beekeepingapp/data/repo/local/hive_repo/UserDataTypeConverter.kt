package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.reedsloan.beekeepingapp.data.local.UserData

class UserDataTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromUserData(userData: UserData): String {
        return gson.toJson(userData)
    }

    @TypeConverter
    fun toUserData(userData: String): UserData {
        return gson.fromJson(userData, UserData::class.java)
    }
}