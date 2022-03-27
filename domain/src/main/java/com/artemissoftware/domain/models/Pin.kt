package com.artemissoftware.domain.models

import java.util.*

data class Pin (
    val id: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val date: Date,
    val address: String){

    companion object{

        fun getMock(): Pin {

            return Pin(
                latitude = 1.35,
                longitude = 103.87,
                date = Date(),
                address = "Greek Pantheon Street"
            )
        }
    }
}
