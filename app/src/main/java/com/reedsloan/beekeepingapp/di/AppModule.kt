package com.reedsloan.beekeepingapp.di

import android.app.Application
import androidx.room.Room
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.HiveDatabase
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.HiveRepositoryImpl
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideHiveRepository(app: Application): HiveRepository {
        return HiveRepositoryImpl(db = provideHiveDatabase(app), app)
    }

    @Singleton
    @Provides
    fun provideHiveDatabase(app: Application): HiveDatabase {
        return Room.databaseBuilder(
            app,
            HiveDatabase::class.java,
            "hive_database"
        ).build()
    }
}