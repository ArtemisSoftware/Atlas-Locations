package com.artemissoftware.atlaslocations.screens

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.artemissoftware.atlaslocations.composables.Tracker
import com.artemissoftware.atlaslocations.screens.mappers.toPin
import com.artemissoftware.atlaslocations.screens.navigation.Screen
import com.google.android.gms.maps.model.LatLng

@Composable
fun TrackingScreen(
    location: Location? = null,
    onCurrentLocation: () -> Unit,
    onStartLocationUpdates: () -> Unit,
    onStopLocationUpdates: () -> Unit,
    viewModel: MapsViewModel = viewModel(),
    navController: NavHostController
){

    val context =  LocalContext.current

    location?.let {

        val changing = LatLng(it.latitude, it.longitude)
        if(viewModel.state.trackState == TrackState.COLLECTING){

            viewModel.currentPin?.let{ pin ->
                viewModel.onEvent(MapEvent.UpdateLocation(changing.toPin(context)))
            } ?: run {
                viewModel.onEvent(MapEvent.SetCurrentPosition(changing.toPin(context, true)))
                onStartLocationUpdates()
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {






        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {


                Text(text = "Current location")

                viewModel.currentPin?.let{

                    Text(text = "Latitude: ${it.latitude}")
                    Text(text = "Longitude: ${it.longitude}")
                }


                Spacer(modifier = Modifier.height(16.dp))


                Tracker(
                    description = viewModel.state.distance,
                    isTracking = viewModel.state.trackState,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)

                )


                if(viewModel.state.pins.size == 2) {
                    val pin = viewModel.state.pins[1]

                    Text(text = "Destination location")
                    Text(text = "Latitude:${pin.latitude}")
                    Text(text = "Longitude: ${pin.longitude}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if(viewModel.state.pins.size == 2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Destination location was found")
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = {

                        navController.navigate(Screen.MapScreen.route)

                    }) {
                        Text("Check pins on the map")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }


                if (viewModel.state.trackState == TrackState.LOCATION_FOUND) {
                    onStopLocationUpdates()
                }

                if (viewModel.state.trackState == TrackState.IDLE || viewModel.state.trackState ==TrackState.LOCATION_FOUND) {
                    OutlinedButton(onClick = {
                        onStartLocationUpdates()
                        viewModel.onEvent(MapEvent.StartTracking)
                    }) {

                        val title = if(viewModel.state.trackState ==TrackState.LOCATION_FOUND) "New search" else "Start looking"
                        Text(text =  title)
                    }
                }
                if(viewModel.state.trackState == TrackState.COLLECTING){

                    OutlinedButton(onClick = {
                        onStopLocationUpdates()
                        viewModel.onEvent(MapEvent.CancelTracking)
                    }) {
                        Text("Cancel tracking")
                    }
                }






            }
        }

    }




}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    //TrackingScreen()
}