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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.artemissoftware.atlaslocations.composables.Tracker
import com.artemissoftware.atlaslocations.screens.mappers.toPin
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@ExperimentalMaterialApi
@Composable
fun MapScreen(
    location: Location? = null,
    viewModel: MapsViewModel = viewModel()
){

    val context =  LocalContext.current
    val pins = viewModel.state.pins


    var singapore = LatLng(1.35, 103.87)
    var changing = LatLng(1.35, 103.87)

    location?.let {
        singapore = LatLng(it.latitude, it.longitude)
        changing = LatLng(it.latitude, it.longitude)
        if(viewModel.state.trackState == TrackState.COLLECTING){

            viewModel.currentPin?.let{ pin ->
                singapore = LatLng(pin.latitude, pin.longitude)
                viewModel.onEvent(MapEvent.UpdateLocation(changing.toPin(context)))
            }



        }

    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 11f)
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }

    val radius = (30 /** scaffoldState.currentFraction*/).dp









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
                    viewModel.onEvent(MapEvent.DeleteHistory)
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

//                if (viewModel.state.showPinHistory) {
//
//                    viewModel.state.pins.forEach { pin ->
//                        Marker(
//                            position = LatLng(pin.latitude, pin.longitude),
//                            title = "Parking spot (${pin.latitude}, ${pin.longitude})",
//                            snippet = "Long click to delete",
//                            onInfoWindowLongClick = {
//                                //viewModel.onEvent(MapEvent.OnInfoWindowLongClick(spot))
//                            },
//                            onClick = {
//                                it.showInfoWindow()
//                                true
//                            },
//                            icon = BitmapDescriptorFactory.defaultMarker(
//                               if(pin.current) BitmapDescriptorFactory.HUE_GREEN else  BitmapDescriptorFactory.HUE_RED
//                            )
//                        )
//                    }
//                }
            }


            Tracker(
                description = viewModel.state.distance,
                isTracking = viewModel.state.trackState,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .align(Alignment.TopStart)
            )

//            OutlinedButton(
//                onClick = {
//                    viewModel.onEvent(MapEvent.ToggleHistoryPins)
//                },
//
//                modifier= Modifier
//                    .padding(top = 16.dp, end = 16.dp)
//                    .size(50.dp)
//                    .align(Alignment.TopEnd),
//                shape = CircleShape,
//                border= BorderStroke(1.dp, Color.Blue),
//                contentPadding = PaddingValues(0.dp),  //avoid the little icon
//                colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
//            ) {
//                Icon(
//                    Icons.Default.History,
//                    contentDescription = "content description")
//            }



            OutlinedButton(
                onClick = {
                    viewModel.onEvent(MapEvent.SetCurrentPosition(singapore.toPin(context, true)))
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
                    Icons.Default.MarkAsUnread,
                    contentDescription = "content description")
            }



        }







    }
}






@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MapScreen()
}
