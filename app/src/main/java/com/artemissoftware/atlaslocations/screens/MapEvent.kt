package com.artemissoftware.atlaslocations.screens

import android.location.Location
import com.artemissoftware.domain.models.Pin

sealed class MapEvent {
    object ToggleMapStyle: MapEvent()
    data class UpdateLocation(val pin: Pin): MapEvent()
    data class SetCurrentPosition(val pin: Pin): MapEvent()

    object SaveDistance: MapEvent()
    object StartTracking: MapEvent()
    object CancelTracking: MapEvent()
}