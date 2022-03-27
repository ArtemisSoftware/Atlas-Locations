package com.artemissoftware.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.artemissoftware.data.database.dao.PinDao
import com.artemissoftware.data.database.entities.PinEntity

@Database(
    entities = [PinEntity::class],
    version = 1
)
abstract class PinDatabase: RoomDatabase() {

    abstract val pinDao: PinDao
}