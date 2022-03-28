package com.artemissoftware.atlaslocations

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemissoftware.atlaslocations.screens.CurrentLocationService
import com.artemissoftware.atlaslocations.screens.MapScreen
import com.artemissoftware.atlaslocations.ui.theme.AtlasLocationsTheme
import com.artemissoftware.atlaslocations.util.SharedPreferenceUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import android.widget.TextView
import com.artemissoftware.atlaslocations.util.Permissions

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationCallback

import com.google.android.gms.location.LocationServices
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


private const val TAG = "MainActivity--"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34


@AndroidEntryPoint
class MainActivity : ComponentActivity(), PermissionListener, OnSuccessListener<LocationSettingsResponse>, OnFailureListener, OnCompleteListener<Void>  {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient;
     lateinit var locationRequest: LocationRequest

    private var mCurrentLocation: Location? = null;

    private lateinit var mLocationSettingsRequest: LocationSettingsRequest;
    private lateinit var mLocationCallback: LocationCallback ;

    // location updates interval - 10sec
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2 * 1000

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 3 * 1000

    private val REQUEST_CHECK_SETTINGS = 100


    private fun init(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this);


        mLocationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // location is received
                Log.d(TAG, "updating")

                locationResult.lastLocation.let {

                    if(it.accuracy < 20){
                        setContent {
                            AtlasLocationsTheme {
                                // A surface container using the 'background' color from the theme
                                Surface(color = MaterialTheme.colors.background) {
                                    MapScreen(location = it)
                                }
                            }
                        }
                    }

                }


                mCurrentLocation = locationResult?.getLastLocation()!!;


            }
        }

        locationRequest = LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        val builder = LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private fun startLocationUpdates() {

        if (!Permissions.checkPermissions(applicationContext)) {
            Permissions.requestPermissions(applicationContext, this)
        }
        else {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this)
                .addOnFailureListener(this)
        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getLastLocation()
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

        response?.isPermanentlyDenied?.let {
            Log.d(TAG, "isPermanentlyDenied")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (!Permissions.checkPermissions(applicationContext)) {
            Permissions.requestPermissions(applicationContext, this)
        }
        else {

            fusedLocationClient.lastLocation.addOnCompleteListener { taskLocation ->

                if (taskLocation.isSuccessful && taskLocation.result != null) {


                    mCurrentLocation = taskLocation.result!!
                    Log.d(TAG, mCurrentLocation.toString())

                }
                else {
                    Log.d(TAG, "no_location_detected")
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onSuccess(LocationSettingsResponse: LocationSettingsResponse?) {

        Log.d(TAG, "start_location_updates")

        Looper.getMainLooper()?.let {
            fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback,
                it
            )
        };

        //--updateLocationUI();
    }


    override fun onFailure(e: Exception) {

        val statusCode = (e as ApiException).getStatusCode()

        when (statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                try {
                    val rae =  e as ResolvableApiException;
                    rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                }
                catch (sie : IntentSender.SendIntentException) {
                    Log.d(TAG, "pending_intent_unable")
                }
            }

            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                Log.d(TAG, "location_settings_inadequate")
            }
        }

        //--updateLocationUI();
    }


    override fun onComplete(task: Task<Void>) {
        Log.d(TAG, "stop_location_updates")
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        init()
        startLocationUpdates()

        setContent {
            AtlasLocationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MapScreen()
                }
            }
        }
    }



}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AtlasLocationsTheme {
        Greeting("Android")
    }
}