package com.example.weatherpilot.presentation.map

sealed interface MapIntent {

    object SaveDataToDataStore : MapIntent

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

    data class AlertDateIntent(val date : String) : MapIntent

    data class ShowSnackBar(val message : String) : MapIntent


    data class SetAlarmDateIntent(val date: String) : MapIntent


    data class SetAlarmTimeIntent(val time : String) : MapIntent


}