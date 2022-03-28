package com.artemissoftware.domain.usecase

import com.artemissoftware.domain.LocationConstants.VALID_DISTANCE_BETWEEN_PINS
import com.artemissoftware.domain.LocationConstants.VALID_DISTANCE_BETWEEN_PINS_MAX
import com.artemissoftware.domain.Resource
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.domain.repositories.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val pinRepository: PinRepository
    ) {

    operator fun invoke(currentPin: Pin, pin : Pin): Flow<Resource<Pin>> = flow {

        var distance = distance(currentPin, pin)

        emit(Resource.Loading(message = distance))

        if(isValidDistance(currentPin, pin)){
            emit(Resource.Success(pin))
        }
    }

    private fun distance(lastPin: Pin, pin: Pin): String {
        return milesToMeters(distance(lastPin.latitude, lastPin.longitude, pin.latitude, pin.longitude)).toString()
    }


    private fun isValidDistance(lastPin: Pin, pin: Pin): Boolean {

        val distance =  milesToMeters(distance(lastPin.latitude, lastPin.longitude, pin.latitude, pin.longitude))

        return distance > VALID_DISTANCE_BETWEEN_PINS && distance < VALID_DISTANCE_BETWEEN_PINS_MAX
    }
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    val METERS_IN_MILE = 1609.344

    fun metersToMiles(meters: Double): Double {
        return meters / METERS_IN_MILE
    }

    fun milesToMeters(miles: Double): Double {
        return miles * METERS_IN_MILE
    }

}