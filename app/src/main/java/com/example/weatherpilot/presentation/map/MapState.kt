package com.example.weatherpilot.presentation.map

import com.example.weatherpilot.domain.model.SearchResponse
import java.time.LocalDateTime


sealed interface MapState {
    data class RegularMapState(
        val longitude: String? = null,
        val latitude: String? = null,
        val saveState: Boolean? = null,
        val insertDataToDataStore: Boolean? = null,
        val mapLoadingState: Boolean = true
    ) : MapState


    data class FavouriteMapState(
        val arabicName: String = "",
        val englishName: String = "",
        val longitude: String = "",
        val latitude: String = "",
        val saveState: Boolean? = null,
        val insertFavouriteResult: Boolean? = null,
        val mapLoadingState: Boolean = true,
    ) : MapState

    data class AlertMapState(
        val arabicName: String = "",
        val englishName: String = "",
        val longitude: String = "",
        val latitude: String = "",
        val date : String = "",
        val time : String = "" ,
        val saveState: Boolean? = null,
        val insertNotification: Boolean? = null,
        val kind : String = "notificationType" ,
        val mapLoadingState: Boolean = true,

    ) : MapState


    data class SearchResultState(val searchResult : SearchResponse? = null,val loading :Boolean? = null)
}

