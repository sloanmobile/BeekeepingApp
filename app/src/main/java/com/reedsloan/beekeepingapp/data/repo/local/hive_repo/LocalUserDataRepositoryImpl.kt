package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import android.app.Application
import android.os.Environment
import android.util.Log
import com.reedsloan.beekeepingapp.data.Message
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.domain.repo.LocalUserDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserDataRepositoryImpl @Inject constructor(
    db: UserDataDatabase,
    private val app: Application
) :
    LocalUserDataRepository {
    private val dao = db.dao

    override suspend fun getUserData(): Result<UserData> {
        return runCatching {
            val result = dao.getUserData()
            Log.d(this::class.simpleName, "getUserData: $result")
            result?.toUserData() ?: UserData()
        }
    }

    /**
     * Exports all hives to a CSV file.
     * CSV file is saved to the app's cache directory.
     * @return The path to the CSV file
     */
    override suspend fun exportToCsv(hive: Hive): Result<Message> {
        // return if hive inspections is empty
        if (hive.hiveInspections.isEmpty()) {
            return Result.failure(Exception("No inspections to export"))
        }


        val inspection = hive.hiveInspections
        val df = dataFrameOf(
            "Date",
            "Notes",
            "Odor",
            "Equipment Condition",
            "Hive Condition",
            "Frames",
            "Foundation Type",
            "Temperament",
            "Population",
            "Queen Cells",
            "Queen Spotted",
            "Queen Marker",
            "Laying Pattern",
            "Brood Stage",
            "Weather Condition",
            "Humidity",
            "Wind Speed",
            "Temperature Fahrenheit",
            "Diseases",
            "Treatments"
        )(
            inspection.map { it.date }.toColumn(),
            inspection.map { it.notes }.toColumn(),
            inspection.map { it.hiveConditions.odor?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.equipmentCondition?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.hiveCondition?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.frames?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.foundationType?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.temperament?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.population?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.queenCells?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.queenSpotted.toString() }.toColumn(),
            inspection.map { it.hiveConditions.queenMarker?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.layingPattern?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.broodStage?.displayValue.toString() }.toColumn(),
            inspection.map { it.hiveConditions.weatherCondition?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.humidity.toString() }.toColumn(),
            inspection.map { it.hiveConditions.windSpeed?.displayValue }.toColumn(),
            inspection.map { it.hiveConditions.temperature.toString() }.toColumn(),
            inspection.map { hiveInspection ->
                hiveInspection.hiveHealth.diseases.map { it?.displayValue }
            }.toColumn(),
            inspection.map { hiveInspection ->
                hiveInspection.hiveHealth.treatments.map { it.treatment.displayValue }
            }.toColumn(),
        )
        // Log saving to CSV
        Log.d(this::class.simpleName, "exportToCsv: $df")

        // create temp file
        withContext(Dispatchers.IO) {
            File.createTempFile(
                "${hive.hiveDetails.name} INSPECTIONS FROM ${hive.hiveInspections.first().date} UNTIL ${hive.hiveInspections.last().date} ",
                ".csv",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            ).apply {
                df.writeCSV(this)
            }
            // Return result success
            Result.success("CSV file saved to Downloads folder")
        }
        // Return result failure
        return Result.failure(Exception("Failed to save CSV file"))
    }

    override suspend fun updateUserData(userData: UserData) {
        dao.updateUserData(userData)
    }

    override suspend fun deleteUserData() {
        dao.deleteUserData()
    }

}