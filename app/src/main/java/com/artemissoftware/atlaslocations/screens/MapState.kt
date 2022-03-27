package com.artemissoftware.atlaslocations.screens

import com.artemissoftware.domain.models.Pin

data class MapState(
    //val properties: MapProperties = MapProperties(),
    val parkingSpots: List<com.artemissoftware.domain.models.Pin> = emptyList(),
    val isStylishMap: Boolean = false
)
