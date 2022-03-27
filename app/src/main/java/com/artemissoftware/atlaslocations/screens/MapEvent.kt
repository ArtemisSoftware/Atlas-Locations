package com.artemissoftware.atlaslocations.screens

sealed class MapEvent {
    object ToggleMapStyle: MapEvent()
//    data class OnMapLongClick(val latLng: LatLng): MapEvent()
//    data class OnInfoWindowLongClick(val spot: ParkingSpot): MapEvent()
}