package com.artemissoftware.atlaslocations.screens.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemissoftware.atlaslocations.composables.PinCard
import com.artemissoftware.domain.LocationConstants
import com.artemissoftware.domain.LocationConstants.VALID_DISTANCE_BETWEEN_PINS
import com.artemissoftware.domain.LocationConstants.calculateDistance
import com.artemissoftware.domain.models.Pin

@Composable
fun PinHistoryPage(
    pins: List<Pin>,
    locationDistance: Int = VALID_DISTANCE_BETWEEN_PINS,
    onRemovePins: () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if(pins.size == 2) "New location found" else "Location Status",
                modifier = Modifier.weight(.80f)
            )

            if(pins.isNotEmpty() && pins.size == 2) {

                IconButton(onClick = {
                    onRemovePins()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save",
                    )
                }
            }
        }




        if(pins.isNotEmpty()) {

            if(pins.size == 2){
                Text(
                    text = "Distance: ${calculateDistance(pins[0], pins[1])} m ",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                PinCard(pin = pins[0])
                PinCard(pin = pins[1])
            }
            else{
                PinCard(pin = pins[0])

                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp),
                    )

                    Text(
                        text = "Location distance $locationDistance m ",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }


            }
        }
        else{
            Text(
                text = "No locations available"
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PinHistoryPage(pins = listOf(Pin.getMock()/*, Pin.getMock()*/), onRemovePins = {} , locationDistance = VALID_DISTANCE_BETWEEN_PINS)
}