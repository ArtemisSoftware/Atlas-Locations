package com.artemissoftware.atlaslocations.screens.mappers

import android.content.Context
import com.artemissoftware.atlaslocations.util.LocationsUtil
import com.artemissoftware.data.database.entities.PinEntity
import com.artemissoftware.domain.models.Pin
import com.google.android.gms.maps.model.LatLng
import java.util.*

fun LatLng.toPin(context: Context, currentPosition: Boolean = false): Pin {
    return Pin(
        latitude = latitude,
        longitude = longitude,
        date = Date(),
        address = LocationsUtil.getAddress(context, this),
        current = currentPosition
    )
}