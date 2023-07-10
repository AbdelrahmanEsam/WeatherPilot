package com.example.weatherpilot.presentation.map

import com.example.weatherpilot.domain.model.AlertItem

sealed interface MapIntent {

    object SaveLocationToDataStore : MapIntent

    object SaveFavourite : MapIntent

    object SaveAlert : MapIntent
    data class MapLoaded(val stateType: String) : MapIntent

    data class NewLatLong(val latitude: String, val longitude: String) : MapIntent


    data class NewFavouriteLocation(
        val arabicName: String,
        val englishName: String,
        val latitude: String,
        val longitude: String
    ) : MapIntent

    data class AlertLocationIntent(
        val arabicName: String,
        val englishName: String,
        val latitude: String,
        val longitude: String
    ) : MapIntent



    data class ShowSnackBar(val message : String) : MapIntent


    data class SetAlarmDateIntent(val date: String) : MapIntent


    data class SetAlarmTimeIntent(val time : String) : MapIntent


    data class UpdateAlertStateToScheduled(val alert : AlertItem) : MapIntent


    data class SearchCityName(val cityName : String) : MapIntent


    object ClearSearchList : MapIntent


}