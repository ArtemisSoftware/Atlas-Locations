package com.artemissoftware.repositories

import com.artemissoftware.data.database.dao.PinDao
import com.artemissoftware.data.mappers.toPin
import com.artemissoftware.data.mappers.toPinEntity
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PinRepositoryImpl @Inject constructor(
    private val dao: PinDao
): PinRepository {

    override suspend fun insertPins(pins: List<Pin>) {
        dao.insertPins(pins.map{ it.toPinEntity() })
    }

    override suspend fun deletePins() {
        dao.deletePins()
    }

    override fun getPins(): Flow<List<Pin>> {
        return dao.getParkingSpots().map { spots ->
            spots.map { it.toPin() }
        }
    }

    override suspend fun getLastLocation(): Pin? {
        return dao.getLastLocation()?.toPin()
    }

    override suspend fun deleteOldestPin() {
        dao.deleteOldestPin()
    }
}