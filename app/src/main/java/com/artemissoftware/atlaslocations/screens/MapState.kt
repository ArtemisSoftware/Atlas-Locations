package com.artemissoftware.atlaslocations.screens

import com.artemissoftware.atlaslocations.models.Pin

data class MapState(
    //val properties: MapProperties = MapProperties(),
    val parkingSpots: List<Pin> = emptyList(),
    val isStylishMap: Boolean = false
)
