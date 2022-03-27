package com.artemissoftware.atlaslocations.screens

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
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@ExperimentalMaterialApi
@Composable
fun MapScreen(
    viewModel: MapsViewModel = viewModel()
){

    val pins = listOf(Pin.getMock(), Pin.getMock())

    val singapore = LatLng(1.35, 103.87)
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

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = viewModel.state.properties,
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
        ) {
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

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MapScreen()
}
