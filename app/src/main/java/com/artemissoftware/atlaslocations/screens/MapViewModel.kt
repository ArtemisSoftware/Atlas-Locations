package com.artemissoftware.atlaslocations.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemissoftware.domain.repositories.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: PinRepository
): ViewModel() {

    var state by mutableStateOf(MapState())

    init {
        viewModelScope.launch {
            repository.getPins().collectLatest { spots ->
                state = state.copy(
                    parkingSpots = spots
                )
            }
        }
    }

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.ToggleMapStyle -> {
                state = state.copy(
//                    properties = state.properties.copy(
//                        mapStyleOptions = if(state.isStylishMap) {
//                            null
//                        } else MapStyleOptions(MapStyle.json),
//                    ),
                    isStylishMap = !state.isStylishMap
                )
            }
//            is MapEvent.OnMapLongClick -> {
//                viewModelScope.launch {
//                    repository.insertParkingSpot(
//                        ParkingSpot(
//                            lat = event.latLng.latitude,
//                            lng = event.latLng.longitude
//                        )
//                    )
//                }
//            }
//            is MapEvent.OnInfoWindowLongClick -> {
//                viewModelScope.launch {
//                    repository.deleteParkingSpot(event.spot)
//                }
//            }
        }
    }
}