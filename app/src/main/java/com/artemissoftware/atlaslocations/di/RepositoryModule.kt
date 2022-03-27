package com.artemissoftware.atlaslocations.di

import com.artemissoftware.data.database.PinDatabase
import com.artemissoftware.domain.repositories.PinRepository
import com.artemissoftware.repositories.PinRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePinRepository(db: PinDatabase): PinRepository {
        return PinRepositoryImpl(db.pinDao)
    }
}