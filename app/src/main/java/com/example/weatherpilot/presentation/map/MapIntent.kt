package com.example.weatherpilot.presentation.map

sealed interface MapIntent
{

    object SaveDataToDataStore : MapIntent

    object SaveFavourite : MapIntent
    object MapLoaded : MapIntent

    data class NewLatLong(val latitude : String, val longitude : String) : MapIntent


    data class NewFavouriteLocation(val arabicName : String , val englishName : String ,val latitude : String, val longitude : String) : MapIntent


}