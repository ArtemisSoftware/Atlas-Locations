package com.artemissoftware.atlaslocations.screens

import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artemissoftware.atlaslocations.screens.pages.PinHistoryPage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.artemissoftware.atlaslocations.composables.Tracker
import com.artemissoftware.atlaslocations.screens.mappers.toPin
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun MapScreen(
    location: Location? = null,
    onCurrentLocation: () -> Unit,
    onStartLocationUpdates: () -> Unit,
    onStopLocationUpdates: () -> Unit,
    viewModel: MapsViewModel = viewModel()
){

    val scope = rememberCoroutineScope()

    val context =  LocalContext.current
    val pins = viewModel.state.pins





    var singapore = LatLng(1.35, 103.87)
    viewModel.currentPin?.let {
        singapore = LatLng(it.latitude, it.longitude)
    }
    var changing = LatLng(1.35, 103.87)

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 11f)
    }

    location?.let {
        singapore = LatLng(it.latitude, it.longitude)
        changing = LatLng(it.latitude, it.longitude)
        if(viewModel.state.trackState == TrackState.COLLECTING){

            viewModel.currentPin?.let{ pin ->
                singapore = LatLng(pin.latitude, pin.longitude)

                val dd = isBetterLocation(location = it, viewModel.lolo)

                if(dd)viewModel.onEvent(MapEvent.UpdateLocation(changing.toPin(context)))
            } ?: run {

                viewModel.lolo = it
                cameraPositionState.position = CameraPosition.fromLatLngZoom(changing, 11f)
                viewModel.onEvent(MapEvent.SetCurrentPosition(changing.toPin(context, true)))
                onStartLocationUpdates()
            }



        }

    }



    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }

    val radius = (30 /** scaffoldState.currentFraction*/).dp


    val sheetToggle: () -> Unit = {
        scope.launch {
            scaffoldState.bottomSheetState.expand()

        }
    }






    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(MapEvent.ToggleMapStyle)
                }
            ) {
                Icon(
                    imageVector = if (viewModel.state.isStylishMap) Icons.Default.ToggleOff else Icons.Default.ToggleOn,
                    contentDescription = "Toggle map style"
                )
            }
        },
        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
        sheetPeekHeight = 30.dp,
        sheetContent = {
            PinHistoryPage(
                pins = pins,
                onRemovePins = {
                    viewModel.onEvent(MapEvent.SaveDistance)
                }
            )
        },

    ) {


        Box() {
            

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = viewModel.state.properties,
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
            ) {

                StartPin(viewModel)



                if (viewModel.state.trackState == TrackState.LOCATION_FOUND) {

                    var avg_Lat = 0.0
                    var avg_Lng = 0.0

                    viewModel.state.pins.forEach { pin ->
                        Marker(
                            position = LatLng(pin.latitude, pin.longitude),
                            title = if(pin.current) "Starting Location" else "End Location",
                            snippet = pin.address,
                            onInfoWindowLongClick = {
                                //viewModel.onEvent(MapEvent.OnInfoWindowLongClick(spot))
                            },
                            onClick = {
                                it.showInfoWindow()
                                true
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(
                               if(pin.current) BitmapDescriptorFactory.HUE_GREEN else  BitmapDescriptorFactory.HUE_RED
                            )
                        )

                        avg_Lat+= pin.latitude
                        avg_Lng+= pin.longitude
                    }

                    avg_Lat /= viewModel.state.pins.size
                    avg_Lng /= viewModel.state.pins.size


                    val latLng = LatLng(avg_Lat, avg_Lng);
                    scope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 22f))
                    }

                    sheetToggle()
                    onStopLocationUpdates()
                }

            }


            Tracker(
                description = viewModel.state.distance,
                isTracking = viewModel.state.trackState,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .align(Alignment.TopStart)
            )

            OutlinedButton(
                onClick = {
                    onCurrentLocation()
                    viewModel.onEvent(MapEvent.StartTracking)
                },
                modifier= Modifier
                    .padding(top = 80.dp, end = 16.dp)
                    .size(50.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                border= BorderStroke(1.dp, Color.Blue),
                contentPadding = PaddingValues(0.dp),  //avoid the little icon
                colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
            ) {
                Icon(
                    Icons.Default.RestartAlt,
                    contentDescription = "content description")
            }



        }







    }
}


@Composable
private fun StartPin(viewModel: MapsViewModel) {

    if (viewModel.state.trackState == TrackState.COLLECTING) {

        viewModel.currentPin?.let{
            Marker(
                position = LatLng(it.latitude, it.longitude),
                title = "Current location",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN
                )
            )
        }
    }
}

private val TWO_MINUTES: Long = 1000

fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
    if (currentBestLocation == null) {
        // A new location is always better than no location
        return true
    }

    // Check whether the new location fix is newer or older
    val timeDelta: Long =
        location.getElapsedRealtimeNanos() - currentBestLocation.getElapsedRealtimeNanos()
    val isSignificantlyNewer: Boolean = timeDelta > TWO_MINUTES
    val isSignificantlyOlder: Boolean = timeDelta < -TWO_MINUTES
    val isNewer = timeDelta > 0

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
        return true
        // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
        return false
    }

    // Check whether the new location fix is more or less accurate
    val accuracyDelta = (location.getAccuracy() - currentBestLocation.getAccuracy())
    val isLessAccurate = accuracyDelta > 0
    val isMoreAccurate = accuracyDelta < 0
    val isSignificantlyLessAccurate = accuracyDelta > 200

    // Check if the old and new location are from the same provider
    val isFromSameProvider = isSameProvider(
        location.getProvider(),
        currentBestLocation.getProvider()
    )

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
        return true
    } else if (isNewer && !isLessAccurate) {
        return true
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
        return true
    }
    return false
}

/** Checks whether two providers are the same  */
private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
    return if (provider1 == null) {
        provider2 == null
    } else provider1 == provider2
}


@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MapScreen(onStopLocationUpdates = {}, onStartLocationUpdates = {}, onCurrentLocation = {})
}
