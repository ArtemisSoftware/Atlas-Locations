package com.artemissoftware.atlaslocations.screens.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemissoftware.atlaslocations.composables.PinCard
import com.artemissoftware.domain.models.Pin

@Composable
fun PinHistoryPage(
    pins: List<Pin>,
    onRemovePins: () -> Unit){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Location History",
                modifier = Modifier.weight(.80f)
            )

            if(pins.isNotEmpty()) {

                IconButton(onClick = {
                    onRemovePins()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                    )
                }
            }
        }

        if(pins.isNotEmpty()) {

            LazyColumn(/*modifier = Modifier.fillMaxHeight()*/) {
                items(pins) { pin ->
                    PinCard(pin = pin)
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
    PinHistoryPage(pins = listOf(Pin.getMock(), Pin.getMock()), onRemovePins = {})
}