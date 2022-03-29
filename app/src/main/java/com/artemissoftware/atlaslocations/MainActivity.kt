package com.artemissoftware.atlaslocations

import android.Manifest
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
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.artemissoftware.atlaslocations.screens.TrackingScreen
import com.artemissoftware.atlaslocations.screens.navigation.MapNavigation
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


private const val TAG = "MainActivity--"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34


@AndroidEntryPoint
class MainActivity : ComponentActivity()
    /*, OnSuccessListener<LocationSettingsResponse>,
    OnFailureListener,
    OnCompleteListener<Void>*/, SharedPreferences.OnSharedPreferenceChangeListener {


    private var foregroundOnlyLocationServiceBound = false
    private var currentOnlyLocationService: CurrentLocationService? = null
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences


    private val foregroundOnlyServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as CurrentLocationService.LocalBinder
            currentOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            currentOnlyLocationService?.subscribeToLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            currentOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }




    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                CurrentLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                //locations=location


                Log.d(TAG, "Current location (${location.latitude}, ${location.longitude})")

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
                                    //startLocationUpdates()
                                },
                                onStopLocationUpdates = {
                                    currentOnlyLocationService?.unSubscribeToLocationUpdates()
                                }
                            )

                        }
                    }
                }
            }
        }
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        if (provideRationale) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    Log.d(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    currentOnlyLocationService?.subscribeToLocationUpdates()
                else -> {
                    //--updateButtonState(false)
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID,
                        null
                    )
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()

        updateButtonState(
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, CurrentLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                CurrentLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }


    private fun updateButtonState(trackingLocation: Boolean) {
        //Update the location here #trackingLocation
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
            updateButtonState(sharedPreferences.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
            )
        }
    }




















    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val enabled = sharedPreferences.getBoolean(
            SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        if (enabled) {
            currentOnlyLocationService?.unSubscribeToLocationUpdates()
        } else {
            if (foregroundPermissionApproved()) {
                currentOnlyLocationService?.subscribeToLocationUpdates()
                    ?: Log.d(TAG, "Service Not Bound")
            } else {
                requestForegroundPermissions()
            }
        }

//        init()
//        startLocationUpdates()

        setContent {
            AtlasLocationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    MapNavigation(
                        onCurrentLocation = {
                            //getLastLocation()
                        },
                        onStartLocationUpdates = {
                            //startLocationUpdates()
                        },
                        onStopLocationUpdates = {
                            currentOnlyLocationService?.unSubscribeToLocationUpdates()
                        }
                    )

                }
            }
        }
    }






//
//
//
//    /**
//     * Provides the entry point to the Fused Location Provider API.
//     */
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//
//    private lateinit var mSettingsClient: SettingsClient;
//    private lateinit var mLocationRequest: LocationRequest;
//    private lateinit var mLocationSettingsRequest: LocationSettingsRequest;
//    private lateinit var mLocationCallback: LocationCallback ;
//    private var mCurrentLocation: Location? = null;
//
//
//    // location updates interval - 10sec
//    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 3 * 1000
//
//    // fastest updates interval - 5 sec
//    // location updates will be received if another app is requesting the locations
//    // than your app can handle
//    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
//
//    private val REQUEST_CHECK_SETTINGS = 100
//
//    // boolean flag to toggle the ui
//    private var  mRequestingLocationUpdates: Boolean = false
//
//
//    /**
//     * Starting location updates
//     * Check whether location settings are satisfied and then
//     * location updates will be requested
//     */
//    private fun startLocationUpdates() {
//
////        if (!Permissions.checkPermissions(applicationContext)) {
////            Permissions.requestPermissions(applicationContext, this)
////        }
////        else {
//            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
//                .addOnSuccessListener(this)
//                .addOnFailureListener(this)
////        }
//    }
//
//
//    private fun init(){
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        mSettingsClient = LocationServices.getSettingsClient(this);
//
//
//        mLocationCallback = object : LocationCallback() {
//
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                // location is received
//                mCurrentLocation = locationResult.getLastLocation();
//
//                updateLocationUI();
//            }
//        }
//
//
//        mRequestingLocationUpdates = false;
//
//
//
//        mLocationRequest = LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        val builder = LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        mLocationSettingsRequest = builder.build();
//
//    }
//
//
//    override fun onSuccess(LocationSettingsResponse: LocationSettingsResponse?) {
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
//        }
//
//
//        updateLocationUI();
//    }
//
//
//    override fun onFailure(e: Exception) {
//
//        val statusCode = (e as ApiException).getStatusCode()
//
//        when (statusCode) {
//            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
//
//                try {
//                    val rae =  e as ResolvableApiException;
//                    rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
//                }
//                catch (sie : IntentSender.SendIntentException) {
//                    //Messages.showSnackbar(this, R.string.pending_intent_unable)
//                    Log.d(TAG, "pending_intent_unable")
//                }
//            }
//
//            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                //--Messages.showSnackbar(this, R.string.location_settings_inadequate)
//                Log.d(TAG, "location_settings_inadequate")
//            }
//        }
//
//        updateLocationUI();
//    }
//
//
//    override fun onComplete(task: Task<Void>) {
//        //Messages.showSnackbar(this, R.string.stop_location_updates)
//        Log.d(TAG, "stop_location_updates")
//    }
//
//    /**
//     * Update the UI displaying the location data and toggling the buttons
//     */
//    private fun updateLocationUI() {
//
//        if(mCurrentLocation != null) {
////            txt_latitude.text = mCurrentLocation?.latitude.toString()
////            txt_longitude.text = mCurrentLocation?.longitude.toString()
////            txt_address.text = getAddress()
////
////            val pin = Pin(
////                mCurrentLocation?.latitude.toString(),
////                mCurrentLocation?.longitude.toString(),
////                Date(),
////                Battery.getBatteryPercentage(applicationContext),
////                getAddress()
////            )
////            pinAdapter.addPin(pin);
//
//            Log.d(TAG, "Current location (${mCurrentLocation!!.latitude}, ${mCurrentLocation!!.longitude})")
//
//            setContent {
//                AtlasLocationsTheme {
//                    // A surface container using the 'background' color from the theme
//                    Surface(color = MaterialTheme.colors.background) {
//                        MapScreen(location = mCurrentLocation)
//                    }
//                }
//            }
//
//        }
//        //--toggleButtons();
//    }

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