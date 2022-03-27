package com.artemissoftware.atlaslocations.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.artemissoftware.domain.models.Pin
import com.artemissoftware.atlaslocations.screens.pages.PinHistoryPage

@ExperimentalMaterialApi
@Composable
fun MapScreen(
){

    val pins = listOf(Pin.getMock(), Pin.getMock())


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val radius = (30 /** scaffoldState.currentFraction*/).dp

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {

        },
        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
        sheetPeekHeight = 30.dp,
        sheetContent = {
            PinHistoryPage(pins = pins)
        },

    ) {


    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MapScreen()
}
