package com.reedsloan.beekeepingapp.data.repo.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    firebase: Firebase,
    private val auth: FirebaseAuth,
    private val gson: Gson
) {
    private val db = firebase.firestore
    private val usersCollection = db.collection("users")

    suspend fun updateUserData(userData: UserData) {
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw Exception("Error updating user data: userId is null")
            val document = userId.let { usersCollection.document(it) }

            Log.d(this::class.simpleName, "Updating data for user ID: $userId")
            // log the data
            Log.d(this::class.simpleName, "userData: $userData")
            val map = mapOf(
                "userPreferences" to gson.toJson(userData.userPreferences),
                "hives" to gson.toJson(userData.hives),
                "lastUpdated" to userData.lastUpdated,
                "userId" to userData.userId
            )
            document
                .set(map, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(this::class.simpleName, "User data successfully updated")
                }
                .addOnFailureListener { e ->
                    Log.w(this::class.simpleName, "Error updating user data", e)
                }.await()
        }
    }

    suspend fun getUserData(): Result<UserData> {
        return runCatching {
            val userId =
                auth.currentUser?.uid ?: throw Exception("Error updating user data: userId is null")
            val document = userId.let { usersCollection.document(it) }

            var userData: UserData? = null
            var data: Map<String, Any>? = null
            document
                .get()
                .addOnSuccessListener { result ->
                    // log the result
                    Log.d(this::class.simpleName, "result data: ${result.data}")

                    data = result.data
                }.await()

            data?.let { result ->
                userData = UserData(
                    userPreferences = gson.fromJson(
                        result["userPreferences"].toString(),
                        UserPreferences::class.java
                    ),
                    hives = Gson().fromJson(
                        result["hives"].toString(),
                        Array<Hive>::class.java
                    ).toList(),
                    lastUpdated = result["lastUpdated"].toString().toLong(),
                    userId = result["userId"].toString()
                )
            }

            userData ?: throw Exception("Error getting user data")
        }
    }
}