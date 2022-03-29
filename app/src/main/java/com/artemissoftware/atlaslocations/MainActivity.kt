package com.artemissoftware.atlaslocations

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.artemissoftware.atlaslocations.screens.navigation.MapNavigation
import com.artemissoftware.atlaslocations.ui.theme.AtlasLocationsTheme
import com.artemissoftware.atlaslocations.util.Permissions
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE
import com.example.easywaylocation.Listener
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity--"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34


@AndroidEntryPoint
class MainActivity : ComponentActivity(), Listener, MultiplePermissionsListener {

    var easyWayLocation: EasyWayLocation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Permissions.checkPermissions(applicationContext)) {
            Permissions.requestPermissions_(applicationContext, this)
        }

        easyWayLocation = EasyWayLocation(this, false, false, this)

        setContent {
            AtlasLocationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    MapNavigation(
                        onCurrentLocation = {
                            //getLastLocation()
                        },
                        onStartLocationUpdates = {
                            easyWayLocation?.startLocation()
                        },
                        onStopLocationUpdates = {
                            easyWayLocation?.endUpdates()
                        }
                    )

                }
            }
        }
    }

    override fun locationOn() {
        Log.d(TAG, "locationOn")
    }

    override fun currentLocation(location: Location?) {

        location?.let {
            Log.d(TAG, "location - ${it.latitude} ${it.longitude}")
        }


        setContent {
            AtlasLocationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    MapNavigation(
                        location = location,
                        onCurrentLocation = {
                            //getLastLocation()
                        },
                        onStartLocationUpdates = {
                            easyWayLocation?.startLocation()
                        },
                        onStopLocationUpdates = {
                            easyWayLocation?.endUpdates()
                        }
                    )

                }
            }
        }

    }

    override fun locationCancelled() {
        Log.d(TAG, "locationCancelled")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCATION_SETTING_REQUEST_CODE -> easyWayLocation!!.onActivityResult(resultCode)
        }
    }


    override fun onPause() {
        super.onPause()
        easyWayLocation?.endUpdates()
    }

    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
        Toast.makeText(
            this, "Permission granted",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionRationaleShouldBeShown(
        p0: MutableList<PermissionRequest>?,
        p1: PermissionToken?
    ) {
        Toast.makeText(
            this, "Permission not granted",
            Toast.LENGTH_LONG
        ).show()
    }

}
