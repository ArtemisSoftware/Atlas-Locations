package com.artemissoftware.domain.repositories

import com.artemissoftware.domain.models.Pin
import kotlinx.coroutines.flow.Flow

interface PinRepository {

    suspend fun insertPins(pins: List<Pin>)

    suspend fun deletePins()

    fun getPins(): Flow<List<Pin>>

    suspend fun getLastLocation(): Pin?

    suspend fun deleteOldestPin()
}