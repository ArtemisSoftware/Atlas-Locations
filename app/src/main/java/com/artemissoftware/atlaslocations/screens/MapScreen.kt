package com.artemissoftware.atlaslocations.screens

import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.atlaslocations.screens.pages.PinHistoryPage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import java.util.*

@ExperimentalMaterialApi
@Composable
fun MapScreen(
    location: Location? = null,
    viewModel: MapsViewModel = viewModel()
){

    val pins = listOf(Pin.getMock(), Pin.getMock())

    var singapore = LatLng(1.35, 103.87)

    location?.let {
        singapore = LatLng(it.latitude, it.longitude)
        viewModel.onEvent(MapEvent.ToggleMapStyle)
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
                    //viewModel.onEvent(MapEvent.ToggleMapStyle)
                    cameraPositionState.move(CameraUpdateFactory.zoomIn())
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
            PinHistoryPage(pins = pins)
        },

    ) {


//        Card (modifier = Modifier.background(Color.Transparent)
//            .padding(12.dp)
//            ,elevation = 10.dp
//        ){
//            OutlinedTextField(
//                value =singapore.latitude.toString(),
//
//                onValueChange = {
//
//                },
//                textStyle = TextStyle(
//                    color = Color.Red,
//                ),
//                modifier = Modifier.background(Color.Transparent)
//                , leadingIcon = {
//                    // In this method we are specifying
//                    // our leading icon and its color.
//                    Icon(
//                        imageVector = Icons.Default.LocationOn,
//                        contentDescription = "image",
//                        tint = Color.Green
//                    )
//                },
//            )
//
//        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = viewModel.state.properties,
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
        ) {


//
//            Card (modifier = Modifier.background(Color.Transparent)
//                .padding(12.dp)
//                ,elevation = 10.dp
//            ){
//                OutlinedTextField(
//                    value =singapore.latitude.toString(),
//
//                    onValueChange = {
//
//                    },
//                    textStyle = TextStyle(
//                        color = Color.Red,
//                    ),
//                    modifier = Modifier.background(Color.Transparent)
//                    , leadingIcon = {
//                        // In this method we are specifying
//                        // our leading icon and its color.
//                        Icon(
//                            imageVector = Icons.Default.LocationOn,
//                            contentDescription = "image",
//                            tint = Color.Green
//                        )
//                    },
//                )
//
//            }
//





            Marker(
                position = LatLng(singapore.latitude, singapore.longitude),
                title = "Current (${singapore.latitude}, ${singapore.longitude}, ${getAddress(LocalContext.current, singapore)})",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN
                )
            )



//            viewModel.state.parkingSpots.forEach { spot ->
//                Marker(
//                    position = LatLng(spot.lat, spot.lng),
//                    title = "Parking spot (${spot.lat}, ${spot.lng})",
//                    snippet = "Long click to delete",
//                    onInfoWindowLongClick = {
//                        viewModel.onEvent(MapEvent.OnInfoWindowLongClick(spot))
//                    },
//                    onClick = {
//                        it.showInfoWindow()
//                        true
//                    },
//                    icon = BitmapDescriptorFactory.defaultMarker(
//                        BitmapDescriptorFactory.HUE_GREEN
//                    )
//                )
//            }
        }












    }
}


private fun getAddress(context: Context, location: LatLng) : String{

    val geocoder = Geocoder(context, Locale.getDefault());

    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
    return addresses[0].getAddressLine(0);
}



@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MapScreen()
}
