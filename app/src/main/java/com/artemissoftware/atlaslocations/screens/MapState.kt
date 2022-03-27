package com.artemissoftware.atlaslocations.screens

import com.artemissoftware.domain.models.Pin
import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties: MapProperties = MapProperties(),
    val parkingSpots: List<Pin> = emptyList(),
    val isStylishMap: Boolean = false
)
