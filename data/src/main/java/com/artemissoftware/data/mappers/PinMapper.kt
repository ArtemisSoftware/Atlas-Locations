package com.artemissoftware.data.mappers

import com.artemissoftware.data.database.entities.PinEntity
import com.artemissoftware.domain.models.Pin
import java.util.*

fun PinEntity.toPin(): Pin {
    return Pin(
        latitude = lat,
        longitude = lng,
        id = id,
        date = Date(),
        address = address
    )
}

fun Pin.toPinEntity(): PinEntity {
    return PinEntity(
        lat = latitude,
        lng = longitude,
        address = address
    )
}