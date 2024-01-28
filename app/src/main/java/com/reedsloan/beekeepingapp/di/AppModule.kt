package com.reedsloan.beekeepingapp.di

import android.app.Application
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.UserDataDatabase
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.LocalUserDataRepositoryImpl
import com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo.UserDataRepositoryImpl
import com.reedsloan.beekeepingapp.domain.repo.LocalUserDataRepository
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository
import com.reedsloan.beekeepingapp.presentation.sign_in.GoogleAuthUiClient
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
    fun provideHiveRepository(app: Application): LocalUserDataRepository {
        return LocalUserDataRepositoryImpl(db = provideHiveDatabase(app), app)
    }

    @Singleton
    @Provides
    fun provideHiveDatabase(app: Application): UserDataDatabase {
        return Room.databaseBuilder(
            app,
            UserDataDatabase::class.java,
            "hive_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideUserDataRepository(): UserDataRepository {
        return UserDataRepositoryImpl(
            firebase = Firebase,
            auth = provideFirebaseAuth(),
            gson = Gson()
        )
    }

    @Singleton
    @Provides
    fun provideGoogleAuthUiClient(app: Application): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = app,
            oneTapClient = Identity.getSignInClient(app),
            auth = provideFirebaseAuth()
        )
    }
}