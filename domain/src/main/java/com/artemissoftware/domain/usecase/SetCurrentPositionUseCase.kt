package com.artemissoftware.domain.usecase

import com.artemissoftware.domain.Resource
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetCurrentPositionUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {

    operator fun invoke(pin: Pin): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        pinRepository.deletePins()
        pinRepository.insertPin(pin)

        emit(Resource.Success(true))
    }

}