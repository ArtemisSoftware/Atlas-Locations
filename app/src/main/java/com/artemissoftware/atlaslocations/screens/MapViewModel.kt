package com.artemissoftware.atlaslocations.screens

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artemissoftware.atlaslocations.util.MapStyle
import com.artemissoftware.domain.Resource
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import com.artemissoftware.domain.usecase.DeletePinsHistoryUseCase
import com.artemissoftware.domain.usecase.SaveDistanceUseCase
import com.artemissoftware.domain.usecase.CalculatePinsDistanceUseCase
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: PinRepository,
    private val calculatePinsDistanceUseCase: CalculatePinsDistanceUseCase,
    private val deletePinsHistoryUseCase: DeletePinsHistoryUseCase,
    private val saveDistanceUseCase: SaveDistanceUseCase
): ViewModel() {


    var state by mutableStateOf(MapState())

    var currentPin by mutableStateOf<Pin?>(null)

    private var searchJob: Job? = null


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
                updateLocation(event.pin)
            }
            is MapEvent.SetCurrentPosition -> {
                setCurrentPosition(event.pin)
            }
            is MapEvent.SaveDistance -> {
                saveDistance()
            }
            is MapEvent.StartTracking -> {
                startTracking()
            }
            is MapEvent.CancelTracking -> {
                cancelTracking()
            }
        }
    }


    private fun startTracking() {

        currentPin = null

        state = state.copy(
            pins = emptyList(),
            trackState = TrackState.COLLECTING
        )
    }

    private fun cancelTracking() {

        currentPin = null

        state = state.copy(
            pins = emptyList(),
            trackState = TrackState.IDLE
        )
    }



    private fun setCurrentPosition(pin: Pin) {

        currentPin = pin

        state = state.copy(
            pins = listOf(pin),
            trackState = TrackState.COLLECTING
        )
    }

    private fun updateLocation(pin: Pin) {

        searchJob?.cancel()
        searchJob = viewModelScope.launch {

            delay(1000L)

            currentPin?.let {

                calculatePinsDistanceUseCase(currentPin = it, pin =pin)
                    .onEach { result ->

                        state = when (result) {

                            is Resource.Loading -> {
                                state.copy(
                                    distance = result.message!!
                                )
                            }

                            is Resource.Success -> {
                                state.copy(
                                    trackState = TrackState.LOCATION_FOUND,
                                    pins = listOf(it, pin)
                                )
                            }
                            else ->{
                                state.copy(
                                    trackState = TrackState.IDLE,
                                )
                            }

                        }
                    }.launchIn(this)
            }


        }
    }


    private fun saveDistance() {

        viewModelScope.launch {

            saveDistanceUseCase(state.pins).onEach { }.launchIn(this)
        }
    }


}