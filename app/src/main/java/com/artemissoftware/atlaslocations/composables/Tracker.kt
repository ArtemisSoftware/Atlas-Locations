package com.artemissoftware.atlaslocations.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Tracker(
    modifier: Modifier = Modifier,
    description: String,
    isTracking: Boolean
) {

    if(isTracking) {

        Card(
            modifier = modifier
                .padding(0.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Row {

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp),
                    )

                    Text(
                        text = description,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {

    Column {
        Tracker(description = "11.2222222", isTracking = true)
        Spacer(modifier = Modifier.height(16.dp))
        Tracker(description = "11.2222222", isTracking = false)
    }

}