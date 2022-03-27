package com.artemissoftware.atlaslocations.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemissoftware.atlaslocations.util.MapStyle
import com.artemissoftware.domain.repositories.PinRepository
import com.artemissoftware.domain.usecase.UpdateLocationUseCase
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: PinRepository,
    private val updateLocationUseCase: UpdateLocationUseCase
): ViewModel() {

    var state by mutableStateOf(MapState())

    init {
//        viewModelScope.launch {
//            repository.getPins().collectLatest { spots ->
//                state = state.copy(
//                    parkingSpots = spots
//                )
//            }
//        }
    }

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.ToggleMapStyle -> {
                state = state.copy(
                    properties = state.properties.copy(
                        mapStyleOptions = if(state.isStylishMap) {
                            null
                        } else MapStyleOptions(MapStyle.json),
                    ),
                    isStylishMap = !state.isStylishMap
                )
            }
            is MapEvent.UpdateLocation -> {
                viewModelScope.launch {

                    updateLocationUseCase(event.pin)
                        .onEach { result ->
                            when (result) {
//                                is Resource.Success -> {
//                                    _state.value = state.value.copy(
//                                        wordInfoItems = result.data ?: emptyList(),
//                                        isLoading = false
//                                    )
//                                }
//                                is Resource.Error -> {
//                                    _state.value = state.value.copy(
//                                        wordInfoItems = result.data ?: emptyList(),
//                                        isLoading = false
//                                    )
//                                    _eventFlow.emit(UIEvent.ShowSnackbar(
//                                        result.message ?: "Unknown error"
//                                    ))
//                                }
//                                is Resource.Loading -> {
//                                    _state.value = state.value.copy(
//                                        wordInfoItems = result.data ?: emptyList(),
//                                        isLoading = true
//                                    )
//                                }
                            }
                        }.launchIn(this)
                }
            }
//            is MapEvent.OnInfoWindowLongClick -> {
//                viewModelScope.launch {
//                    repository.deleteParkingSpot(event.spot)
//                }
//            }
        }
    }
}