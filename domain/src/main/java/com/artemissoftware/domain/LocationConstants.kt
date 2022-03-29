package com.artemissoftware.domain

import com.artemissoftware.domain.models.Pin

object LocationConstants {

    const val MAX_LOCATIONS = 5
    const val VALID_DISTANCE_BETWEEN_PINS = 0 //in meters
    const val VALID_DISTANCE_BETWEEN_PINS_MAX = VALID_DISTANCE_BETWEEN_PINS + 1 //in meters

    const val METERS_IN_MILE = 1609.344


    // location updates interval - 10sec
    const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 3 * 1000

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1 * 1000


    const val ACCURACY = 15F

    fun calculateDistance(lastPin: Pin, pin: Pin): Double {
        return milesToMeters(calculateDistance(lastPin.latitude, lastPin.longitude, pin.latitude, pin.longitude))
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }


    fun metersToMiles(meters: Double): Double {
        return meters / METERS_IN_MILE
    }

    fun milesToMeters(miles: Double): Double {
        return miles * METERS_IN_MILE
    }

}