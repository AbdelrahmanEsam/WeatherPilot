package com.example.weatherpilot.presentation.map

sealed interface MapIntent
{

    object SaveDataToDataStore : MapIntent
    object MapLoaded : MapIntent

    data class NewLatLong(val latitude : String, val longitude : String) : MapIntent
}