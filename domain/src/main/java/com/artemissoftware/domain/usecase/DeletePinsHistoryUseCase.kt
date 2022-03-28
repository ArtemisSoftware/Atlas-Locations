package com.artemissoftware.domain.usecase

import com.artemissoftware.domain.Resource
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeletePinsHistoryUseCase @Inject constructor(
    private val pinRepository: PinRepository
) {

    operator fun invoke(): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        pinRepository.deletePins()

        emit(Resource.Success(true))
    }


}