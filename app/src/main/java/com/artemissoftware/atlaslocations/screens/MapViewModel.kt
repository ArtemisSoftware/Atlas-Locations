package com.artemissoftware.atlaslocations.screens

import androidx.compose.runtime.State
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
import com.artemissoftware.domain.usecase.SetCurrentPositionUseCase
import com.artemissoftware.domain.usecase.UpdateLocationUseCase
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
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val deletePinsHistoryUseCase: DeletePinsHistoryUseCase,
    private val setCurrentPositionUseCase: SetCurrentPositionUseCase
): ViewModel() {

    var state by mutableStateOf(MapState())

    var currentPin by mutableStateOf<Pin?>(null)

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getPins().collectLatest { spots ->
                state = state.copy(
                    pins = spots
                )
            }
        }
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

            is MapEvent.ToggleHistoryPins -> {
                state = state.copy(
                    showPinHistory = !state.showPinHistory
                )
            }

            is MapEvent.UpdateLocation -> {
                updateLocation(event.pin)
            }
            is MapEvent.DeleteHistory -> {
                deleteHistory()
            }
            is MapEvent.SetCurrentPosition -> {
                setCurrentPosition(event.pin)
            }
        }
    }

    private fun setCurrentPosition(pin: Pin) {

        currentPin = pin

        viewModelScope.launch {

            setCurrentPositionUseCase(pin)
                .onEach { result ->

                    state = when (result) {

                        is Resource.Loading -> {
                            state.copy(
                                isLoading = true,
                                pins = emptyList()
                            )
                        }
                        else ->{
                            state.copy(
                                isLoading = false,
                                collecting = true,
                            )
                        }

                    }
                }.launchIn(this)
        }
    }

    private fun updateLocation(pin: Pin) {

        searchJob?.cancel()
        searchJob = viewModelScope.launch {

            delay(1000L)

            updateLocationUseCase(pin)
                .onEach { result ->

                    state = when (result) {

                        is Resource.Loading -> {
                            state.copy(
                                isLoading = true
                            )
                        }
                        else ->{
                            state.copy(
                                isLoading = false,
                                distance = result.data!!
                            )
                        }

                    }
                }.launchIn(this)
        }
    }


    private fun deleteHistory() {

        currentPin = null

        viewModelScope.launch {

            deletePinsHistoryUseCase()
                .onEach { result ->

                    state = when (result) {

                        is Resource.Loading -> {
                            state.copy(
                                isLoading = true
                            )
                        }
                        else ->{
                            state.copy(
                                isLoading = false,
                                pins = emptyList(),
                                collecting = false
                            )
                        }

                    }
                }.launchIn(this)
        }
    }

}