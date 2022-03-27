package com.artemissoftware.data.database.dao

import androidx.room.*
import com.artemissoftware.data.database.entities.PinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPin(pin: PinEntity)

    @Query("DELETE FROM pinentity")
    suspend fun deletePins()

    @Query("SELECT * FROM pinentity")
    fun getParkingSpots(): Flow<List<PinEntity>>
}