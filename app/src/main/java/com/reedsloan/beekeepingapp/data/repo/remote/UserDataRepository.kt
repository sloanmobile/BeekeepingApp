package com.reedsloan.beekeepingapp.data.repo.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserDataRepository(
    private val firebase: Firebase,
    private val auth: FirebaseAuth,
    private val gson: Gson
) {
    private val db = firebase.firestore
    private val userId = auth.currentUser!!.uid

    suspend fun updateUserData(userData: UserData) {
        withContext(Dispatchers.IO) {

            val user = hashMapOf(
                "userPreferences" to gson.toJson(userData.userPreferences),
                "hives" to gson.toJson(userData.hives),
                "lastUpdated" to userData.lastUpdated,
                "userId" to userId
            )

            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
        }
    }

    suspend fun getUserData(): UserData? {
        return withContext(Dispatchers.IO) {
            var userData: UserData? = null
            
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    val data = result.first().data

                    userData = UserData(
                        userPreferences = gson.fromJson(data["userPreferences"].toString(), UserPreferences::class.java),
                        hives = gson.fromJson(data["hives"].toString(), Array<Hive>::class.java).toList(),
                        lastUpdated = data["lastUpdated"].toString().toLong(),
                        userId = data["userId"].toString()
                    )
                }

            return@withContext userData
        }
    }
}