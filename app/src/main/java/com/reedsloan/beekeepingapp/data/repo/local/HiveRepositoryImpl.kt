package com.reedsloan.beekeepingapp.data.repo.local

import android.app.Application
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserPreferencesEntity
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.mapper.toHive
import com.reedsloan.beekeepingapp.data.mapper.toHiveEntity
import com.reedsloan.beekeepingapp.data.mapper.toUserPreferences
import com.reedsloan.beekeepingapp.data.mapper.toUserPreferencesEntity
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
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
    override suspend fun exportToCsv(): String {
        val hives = getAllHives()
        val df = DataFrame.Empty

        // HiveInfo
        // id column
        val idColumn = df.add("id") { hives.map { it.hiveInfo.id } }
        // name column
        val nameColumn = df.add("name") { hives.map { it.hiveInfo.name } }
        // location column
        val locationColumn = df.add("location") { hives.map { it.hiveInfo.location } }
        // notes column
        val notesColumn = df.add("notes") { hives.map { it.hiveInfo.notes } }
        // image column
        val imageColumn = df.add("image") { hives.map { it.hiveInfo.image } }
        // date column
        val timestampColumn = df.add("timestamp") { hives.map { it.hiveInfo.timestamp } }
        // weather column
        val weatherColumn = df.add("weather") { hives.map { it.hiveInfo.weather } }
        // temperatureFahrenheit column
        val temperatureFahrenheitColumn =
            df.add("temperatureFahrenheit") { hives.map { it.hiveInfo.temperatureFahrenheit } }

        // HiveConditions
        // odor column
        val odorColumn = df.add("odor") { hives.map { it.hiveConditions.odor?.displayValue ?: ""} }
        // equipment condition column
        val equipmentConditionColumn =
            df.add("equipmentCondition") { hives.map { it.hiveConditions.equipmentCondition?.displayValue
                ?: "" } }
        // hive condition column
        val hiveConditionColumn =
            df.add("hiveCondition") { hives.map { it.hiveConditions.hiveCondition?.displayValue ?: "" } }
        // frames and combs column
        val framesAndCombsColumn =
            df.add("framesAndCombs") { hives.map { it.hiveConditions.framesAndCombs?.displayValue ?: "" } }
        // foundation type column
        val foundationTypeColumn =
            df.add("foundationType") { hives.map { it.hiveConditions.foundationType?.displayValue ?: "" } }
        // temperament column
        val temperamentColumn =
            df.add("temperament") { hives.map { it.hiveConditions.temperament?.displayValue ?: "" } }
        // population column
        val populationColumn =
            df.add("population") { hives.map { it.hiveConditions.population?.displayValue ?: "" } }
        // queen cells column
        val queenCellsColumn =
            df.add("queenCells") { hives.map { it.hiveConditions.queenCells?.displayValue ?: "" } }
        // queen spotted column
        val queenSpottedColumn =
            df.add("queenSpotted") { hives.map { it.hiveConditions.queenSpotted } }
        // queen marker column
        val queenMarkerColumn =
            df.add("queenMarker") { hives.map { it.hiveConditions.queenMarker } }
        // laying pattern column
        val layingPatternColumn =
            df.add("layingPattern") { hives.map { it.hiveConditions.layingPattern?.displayValue ?: "" } }
        // brood stage column
        val broodStageColumn =
            df.add("broodStage") { hives.map { it.hiveConditions.broodStage?.displayValue ?: "" } }

        // HiveHealth
        // diseases column
        val diseasesColumn =
            df.add("diseases") { hives.map { hive -> hive.hiveHealth.diseases.map { it.displayValue } } }
        // treatments column
        val treatmentsColumn =
            df.add("treatments") { hives.map { hive -> hive.hiveHealth.treatments.map { it.treatment.displayValue } } }
        // treatment dates column
        val treatmentDatesColumn =
            df.add("treatmentDates") { hives.map { hive -> hive.hiveHealth.treatments.map { it.dateApplied } } }
        // treatment date removed column
        val treatmentDateRemovedColumn =
            df.add("treatmentDateRemoved") { hives.map { hive -> hive.hiveHealth.treatments.map { it.dateRemoved } } }
        // treatment notes column
        val treatmentNotesColumn =
            df.add("treatmentNotes") { hives.map { hive -> hive.hiveHealth.treatments.map { it.treatmentNotes } } }
        // ipm column
        val treatmentIpmColumn =
            df.add("treatmentIpm") { hives.map { hive -> hive.hiveHealth.treatments.map { it.ipm } } }

        // HiveFeeding
        // honey stores column
        val honeyStoresColumn =
            df.add("honeyStores") { hives.map { it.feeding.honeyStores?.displayValue ?: "" } }
        // pollen column
        val pollenColumn = df.add("pollen") { hives.map { it.feeding.pollen?.displayValue ?: "" } }
        // honey b healthy column
        val honeyBHealthyColumn = df.add("honeyBHealthy") { hives.map { it.feeding.honeyBHealthy } }
        // mega bee column
        val megaBeeColumn = df.add("megaBee") { hives.map { it.feeding.megaBee } }
        // vitaFeedGold column
        val vitaFeedGoldColumn = df.add("vitaFeedGold") { hives.map { it.feeding.vitaFeedGold } }
        // sugar syrup column
        val sugarSyrupColumn = df.add("sugarSyrup") { hives.map { it.feeding.sugarSyrup } }

        // HiveNotes
        // notes column
        val hiveNotesColumn = df.add("notes") { hives.map { it.hiveNotes.notes } }

        // create temp file
        withContext(Dispatchers.IO) {
            File.createTempFile("hive_export", ".csv", app.cacheDir)
        }.apply {
            // write to file
            df.writeCSV(this)
            // return path
            return this.absolutePath
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