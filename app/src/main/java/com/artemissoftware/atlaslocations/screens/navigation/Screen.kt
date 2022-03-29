package com.artemissoftware.atlaslocations.screens.navigation

sealed class Screen(val route: String){

    object TrackScreen: Screen("track_screen")
    object MapScreen: Screen("map_screen")
}