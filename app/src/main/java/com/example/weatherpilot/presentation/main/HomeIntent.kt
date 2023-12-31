package com.example.weatherpilot.presentation.main

sealed interface HomeIntent
{
    data class NewLocationFromGPS(val longitude : String, val latitude  :  String) : HomeIntent
    object ReadLatLongFromDataStore : HomeIntent

    object FetchData : HomeIntent

    object  ReadPrefsFromDataStore : HomeIntent

    data class FetchDataOfFavouriteLocation(val longitude : String, val latitude  :  String) : HomeIntent



}