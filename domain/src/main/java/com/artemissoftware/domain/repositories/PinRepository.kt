package com.artemissoftware.domain.repositories

import com.artemissoftware.domain.models.Pin
import kotlinx.coroutines.flow.Flow

interface PinRepository {

    suspend fun insertPin(pin: Pin)

    suspend fun deletePins()

    fun getPins(): Flow<List<Pin>>
}