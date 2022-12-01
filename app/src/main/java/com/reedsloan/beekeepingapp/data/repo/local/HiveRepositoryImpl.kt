package com.reedsloan.beekeepingapp.data.repo.local

import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.mapper.toHive
import com.reedsloan.beekeepingapp.data.mapper.toHiveEntity
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HiveRepositoryImpl @Inject constructor(db: HiveDatabase): HiveRepository {
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

    override suspend fun deleteHive(hiveId: Int) {
        dao.deleteHive(hiveId)
    }

    override suspend fun deleteAllHives() {
        dao.deleteAllHives()
    }
}