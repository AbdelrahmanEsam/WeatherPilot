package com.example.weatherpilot.presentation.map


sealed interface MapState {
    data class RegularMapState(
        val longitude: String = "",
        val latitude: String = "",
        val saveState: Boolean? = null,
        val mapLoadingState: Boolean = true
    ) : MapState


    data class FavouriteMapState(
        val arabicName : String = "",
        val englishName : String = "",
        val longitude: String = "",
        val latitude: String = "",
        val saveState: Boolean? = null,
        val mapLoadingState: Boolean = true
    ) : MapState
}

