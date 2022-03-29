package com.artemissoftware.domain.usecase

import com.artemissoftware.domain.Resource
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveDistanceUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {

    operator fun invoke(pins: List<Pin>) = flow {

        pinRepository.insertPins(pins)
        emit(Resource.Success(true))
    }

}