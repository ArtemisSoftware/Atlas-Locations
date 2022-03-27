package com.artemissoftware.atlaslocations.screens

import android.location.Location

sealed class MapEvent {
    object ToggleMapStyle: MapEvent()
    data class UpdateLocation(val location: Location): MapEvent()
//    data class OnMapLongClick(val latLng: LatLng): MapEvent()
//    data class OnInfoWindowLongClick(val spot: ParkingSpot): MapEvent()
}