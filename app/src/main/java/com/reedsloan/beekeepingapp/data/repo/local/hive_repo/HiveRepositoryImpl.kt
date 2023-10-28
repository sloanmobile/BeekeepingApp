package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import android.app.Application
import android.os.Environment
import android.util.Log
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.hive.BroodStage
import com.reedsloan.beekeepingapp.data.local.hive.EquipmentCondition
import com.reedsloan.beekeepingapp.data.local.hive.FoundationType
import com.reedsloan.beekeepingapp.data.local.hive.FramesAndCombs
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.hive.HiveCondition
import com.reedsloan.beekeepingapp.data.local.hive.LayingPattern
import com.reedsloan.beekeepingapp.data.local.hive.Odor
import com.reedsloan.beekeepingapp.data.local.hive.Population
import com.reedsloan.beekeepingapp.data.local.hive.QueenCells
import com.reedsloan.beekeepingapp.data.local.hive.QueenMarker
import com.reedsloan.beekeepingapp.data.local.hive.Temperament
import com.reedsloan.beekeepingapp.data.local.hive.WeatherCondition
import com.reedsloan.beekeepingapp.data.local.hive.WindSpeed
import com.reedsloan.beekeepingapp.data.mapper.toHive
import com.reedsloan.beekeepingapp.data.mapper.toHiveEntity
import com.reedsloan.beekeepingapp.data.mapper.toUserPreferences
import com.reedsloan.beekeepingapp.data.mapper.toUserPreferencesEntity
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HiveRepositoryImpl @Inject constructor(db: HiveDatabase, private val app: Application) :
    HiveRepository {
    private val dao = db.dao

    override suspend fun getHive(hiveId: Int): Hive {
        return dao.getHive(hiveId).toHive()
    }

    override suspend fun getAllHives(): List<Hive> {
        return dao.getAllHives().map { it.toHive() }
    }

    override suspend fun createHive(hive: Hive) {
        dao.insertHive(hive.toHiveEntity())
    }

    override suspend fun updateHive(hive: Hive) {
        dao.updateHive(hive.toHiveEntity())
    }

    override suspend fun deleteHive(hiveId: String) {
        dao.deleteHive(hiveId)
    }

    override suspend fun deleteAllHives() {
        dao.deleteAllHives()
    }

    /**
     * Exports all hives to a CSV file.
     * CSV file is saved to the app's cache directory.
     * @return The path to the CSV file
     */
    override suspend fun exportToCsv(): Result<String> {
        val hives = getAllHives()
        val allHiveInspections = hives.map { it.hiveInspections }
        return runCatching {
            allHiveInspections.forEachIndexed { index, inspection ->
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
                    inspection.map { it.hiveConditions.equipmentCondition?.displayValue }
                        .toColumn(),
                    inspection.map { it.hiveConditions.hiveCondition?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.frames?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.foundationType?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.temperament?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.population?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.queenCells?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.queenSpotted.toString() }.toColumn(),
                    inspection.map { it.hiveConditions.queenMarker?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.layingPattern?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.broodStage?.displayValue.toString() }
                        .toColumn(),
                    inspection.map { it.hiveConditions.weatherCondition?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.humidity.toString() }.toColumn(),
                    inspection.map { it.hiveConditions.windSpeed?.displayValue }.toColumn(),
                    inspection.map { it.hiveConditions.temperatureFahrenheit.toString() }
                        .toColumn(),
                    inspection.map { hiveInspection -> hiveInspection.hiveHealth.diseases.map { it?.displayValue } }
                        .toColumn(),
                    inspection.map { hiveInspection -> hiveInspection.hiveHealth.treatments.map { it.treatment.displayValue } }
                        .toColumn(),
                )
                // Log saving to CSV
                Log.d(this::class.simpleName, "exportToCsv: $df")

                // create temp file
                withContext(Dispatchers.IO) {
                    val downloadsDir: File =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val hive = hives[index]

                    File(
                        downloadsDir,
                        "${hive.hiveDetails.name}_inspections_export_${hive.hiveInspections.first().date}_until_${hive.hiveInspections.last().date}.csv"
                    )
                }.apply {
                    // write to file
                    df.writeCSV(this)
                    // return path
                    return Result.success(this.absolutePath)
                }
            }
            return Result.failure(Exception("No hives to export"))
        }
    }


    override suspend fun getUserPreferences(): UserPreferences {
        return dao.getUserPreferences().toUserPreferences()
    }

    override suspend fun updateUserPreferences(userPreferences: UserPreferences) {
        dao.updateUserPreferences(userPreferences.toUserPreferencesEntity())
    }

    override suspend fun resetUserPreferences() {
        // initialize a default user preferences object
        dao.updateUserPreferences(UserPreferences().toUserPreferencesEntity())
    }
}