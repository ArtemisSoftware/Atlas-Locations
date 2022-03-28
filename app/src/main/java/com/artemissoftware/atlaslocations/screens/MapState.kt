package com.artemissoftware.atlaslocations.screens

import com.artemissoftware.domain.models.Pin
import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties: MapProperties = MapProperties(),
    val pins: List<Pin> = emptyList(),
    val isStylishMap: Boolean = false,
    val showPinHistory: Boolean = false,
    val isLoading: Boolean = false,
    val collecting: Boolean = false,
    val distance: String = "*",
)
