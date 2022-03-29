package com.artemissoftware.atlaslocations.screens

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.artemissoftware.atlaslocations.R
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
                .padding(8.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {


                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "",
                    tint = Color.Red,
                    modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))


                viewModel.currentPin?.let{

                    Text(text = stringResource(R.string.current_location))
                    Text(text = stringResource(R.string.latitude) +": ${it.latitude}")
                    Text(text = stringResource(R.string.longitude) + ": ${it.longitude}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Tracker(
                    description = viewModel.state.distance,
                    isTracking = viewModel.state.trackState,
                )


                if(viewModel.state.pins.size == 2) {
                    val pin = viewModel.state.pins[1]

                    Text(text = stringResource(R.string.destination_location))
                    Text(text = stringResource(R.string.latitude) +": ${pin.latitude}")
                    Text(text = stringResource(R.string.longitude) + ": ${pin.longitude}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if(viewModel.state.pins.size == 2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.destination_location_found))
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = {

                        navController.navigate(Screen.MapScreen.route)

                    }) {
                        Text(stringResource(R.string.check_pins_on_map))
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

                        val title = if(viewModel.state.trackState ==TrackState.LOCATION_FOUND) stringResource(
                                                    R.string.new_search) else stringResource(
                                                    R.string.start_looking)
                        Text(text =  title)
                    }
                }
                if(viewModel.state.trackState == TrackState.COLLECTING){

                    OutlinedButton(onClick = {
                        onStopLocationUpdates()
                        viewModel.onEvent(MapEvent.CancelTracking)
                    }) {
                        Text(stringResource(R.string.cancel_tracking))
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