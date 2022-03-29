package com.artemissoftware.atlaslocations.screens.navigation

import android.location.Location
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.artemissoftware.atlaslocations.screens.MapScreen
import com.artemissoftware.atlaslocations.screens.MapsViewModel
import com.artemissoftware.atlaslocations.screens.TrackingScreen


@ExperimentalMaterialApi
@Composable
fun MapNavigation(
    viewModel: MapsViewModel = viewModel(),
    location: Location? = null,
    onCurrentLocation: () -> Unit,
    onStartLocationUpdates: () -> Unit,
    onStopLocationUpdates: () -> Unit,
){

    val navHostController = rememberNavController()
    NavHost(navController = navHostController, startDestination = Screen.TrackScreen.route){

        composable(route = Screen.TrackScreen.route){
            TrackingScreen(
                navController = navHostController,
                viewModel = viewModel,
                location = location,
                onCurrentLocation = onCurrentLocation,
                onStartLocationUpdates = onStartLocationUpdates,
                onStopLocationUpdates = onStopLocationUpdates
            )
        }

        composable(route = Screen.MapScreen.route){
            MapScreen(
                //navController = navHostController
                viewModel = viewModel
            )
        }

    }

}