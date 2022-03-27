package com.artemissoftware.atlaslocations.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemissoftware.domain.models.Pin

@Composable
fun PinCard(pin: Pin){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(15.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "",
                tint = Color.Blue,
                modifier = Modifier.padding(8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                PinDetail( description = pin.address)
                PinDetail(title = "latitude", description = pin.latitude.toString())
                PinDetail(title = "longitude", description = pin.longitude.toString())
                PinDetail(description = pin.date.toString())
            }

        }
    }
}

@Composable
private fun PinDetail(title: String? = null, description: String){

    Row() {

        title?.let {
            Text("$title: ")
        }
        Text(description)
    }

}


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PinCard(pin = com.artemissoftware.domain.models.Pin.getMock())
}