package com.artemissoftware.domain.usecase

import com.artemissoftware.domain.LocationHelper.VALID_DISTANCE_BETWEEN_PINS
import com.artemissoftware.domain.LocationHelper.calculateDistance
import com.artemissoftware.domain.Resource
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CalculatePinsDistanceUseCase @Inject constructor(
    private val pinRepository: PinRepository
    ) {

    operator fun invoke(currentPin: Pin, pin : Pin): Flow<Resource<Pin>> = flow {

        var distance = calculateDistance(currentPin, pin)

        emit(Resource.Loading(message = distance.toString()))

        if(isValidDistance(distance)){
            emit(Resource.Success(pin))
        }
    }




    private fun isValidDistance(distance: Double): Boolean {
        return distance > VALID_DISTANCE_BETWEEN_PINS
    }



}