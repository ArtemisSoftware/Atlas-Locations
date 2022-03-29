package com.artemissoftware.atlaslocations.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artemissoftware.atlaslocations.R
import com.artemissoftware.atlaslocations.screens.pages.PinHistoryPage
import com.artemissoftware.domain.models.Pin
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@ExperimentalMaterialApi
@Composable
fun MapScreen(
    viewModel: MapsViewModel = viewModel()
){

    val pins = viewModel.state.pins

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(getLatLngForZoom(pins), 16f)
    }


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
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

                CurrentPins(pins = pins)
            }


            OutlinedButton(
                onClick = {
                    viewModel.onEvent(MapEvent.ToggleMapStyle)
                },
                modifier= Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .size(50.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                border= BorderStroke(1.dp, Color.Blue),
                contentPadding = PaddingValues(0.dp),  //avoid the little icon
                colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
            ) {
                Icon(
                    imageVector = if (viewModel.state.isStylishMap) Icons.Default.ToggleOff else Icons.Default.ToggleOn,
                    contentDescription = "content description")
            }
        }
    }
}


@Composable
private fun CurrentPins(pins: List<Pin>) {

    pins.forEach { pin ->
        Marker(
            position = LatLng(pin.latitude, pin.longitude),
            title = if(pin.current) stringResource(R.string.starting_location) else stringResource(R.string.end_location),
            snippet = pin.address,
            onClick = {
                it.showInfoWindow()
                true
            },
            icon = BitmapDescriptorFactory.defaultMarker(
                if(pin.current) BitmapDescriptorFactory.HUE_GREEN else  BitmapDescriptorFactory.HUE_RED
            )
        )
    }
}





private fun getLatLngForZoom(pins: List<Pin>): LatLng {

    var averageLatitude = 0.0
    var averageLongitude = 0.0

    pins.forEach { pin ->
        averageLatitude+= pin.latitude
        averageLongitude+= pin.longitude
    }

    averageLatitude /= pins.size
    averageLongitude /= pins.size

    return LatLng(averageLatitude, averageLongitude);
}


@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    //MapScreen(onStopLocationUpdates = {}, onStartLocationUpdates = {}, onCurrentLocation = {})
}
