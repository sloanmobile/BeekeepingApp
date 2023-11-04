package com.reedsloan.beekeepingapp.di

import android.app.Application
import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.HiveDatabase
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.HiveRepositoryImpl
import com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo.UserDataRepositoryImplTest
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository
import com.reedsloan.beekeepingapp.presentation.sign_in.GoogleAuthUiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Assert.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleTest {
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
            "hive_database_test"
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
        return UserDataRepositoryImplTest()
    }

    @Singleton
    @Provides
    fun provideGoogleAuthUiClient(app: Application): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = app,
            oneTapClient = Identity.getSignInClient(app),
            auth = AppModule.provideFirebaseAuth()
        )
    }
}