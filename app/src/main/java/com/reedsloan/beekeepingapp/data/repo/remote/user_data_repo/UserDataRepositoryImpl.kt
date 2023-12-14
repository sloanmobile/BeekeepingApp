package com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.tasks.Task
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepositoryImpl @Inject constructor(
    firebase: Firebase,
    private val auth: FirebaseAuth,
    private val gson: Gson
) : UserDataRepository {
    private val db = firebase.firestore
    private val usersCollection = db.collection("users")

    override suspend fun updateUserData(userData: UserData) {
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
                "userId" to userData.userId,
                "tasks" to gson.toJson(userData.tasks)
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

    override suspend fun getUserData(): Result<UserData> {
        return runCatching {
            val userId =
                auth.currentUser?.uid ?: throw Exception("Error updating user data: userId is null")
            val document = userId.let { usersCollection.document(it) }

            var userData: UserData? = null
            document
                .get()
                .addOnSuccessListener { result ->
                    // log the result
                    Log.d(this::class.simpleName, "result data: ${result.data}")
                    result.data?.let { data ->
                        userData = UserData(
                            userPreferences = gson.fromJson(
                                data["userPreferences"].toString(),
                                UserPreferences::class.java
                            ),
                            hives = Gson().fromJson(
                                data["hives"].toString(),
                                Array<Hive>::class.java
                            ).toList(),
                            lastUpdated = data["lastUpdated"].toString().toLong(),
                            userId = data["userId"].toString(),
                            tasks = data["tasks"].toString().let { tasks ->
                                Log.w(this::class.simpleName, "tasks: $tasks")
                                if (tasks != "null") {
                                    Gson().fromJson(
                                        tasks,
                                        Array<Task>::class.java
                                    ).toList()
                                } else {
                                    emptyList()
                                }
                            }
                        )
                    }
                }.await()

            userData ?: throw Exception("Error getting user data")
        }
    }
}