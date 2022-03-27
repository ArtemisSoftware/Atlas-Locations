package com.artemissoftware.domain.usecase

import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val pinRepository: PinRepository) {

    operator fun invoke(pin : Pin): Flow</*Resource<UserProfile>*/Boolean> = flow {

        //emit(Resource.Loading())
        emit(true)
        val lastPin = pinRepository.getLastLocation()


        lastPin?.let{
            if(isValidDistance(lastPin, pin)){

                pinRepository.deleteOldestPin()
                pinRepository.insertPin(pin)
            }
        } ?: kotlin.run {
            pinRepository.insertPin(pin)
        }

        //emit(Resource.Success(userProfile!!))
        emit(true)
    }

    private fun isValidDistance(lastPin: Pin, pin: Pin): Boolean {
        return true
    }


}