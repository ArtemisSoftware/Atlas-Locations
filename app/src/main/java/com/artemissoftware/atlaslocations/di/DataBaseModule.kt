package com.artemissoftware.atlaslocations.di

import android.app.Application
import androidx.room.Room
import com.artemissoftware.data.database.PinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun providePinDatabase(app: Application): PinDatabase {
        return Room.databaseBuilder(
            app,
            PinDatabase::class.java,
            "atlas_locations.db"
        ).build()
    }
}