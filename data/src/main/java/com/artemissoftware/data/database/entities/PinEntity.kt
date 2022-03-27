package com.artemissoftware.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PinEntity(
    val lat: Double,
    val lng: Double,
    val address: String,
    @PrimaryKey val id: Int? = null
)
