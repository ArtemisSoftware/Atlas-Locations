package com.artemissoftware.atlaslocations.util

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.*

object LocationsUtil {

    fun getAddress(context: Context, location: LatLng) : String{

        val geocoder = Geocoder(context, Locale.getDefault());

        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        return addresses[0].getAddressLine(0);
    }
}