package com.example.weatherpilot.presentation.main

sealed interface HomeIntent
{
    data class NewLocationFromGPS(val longitude : String, val latitude  :  String) : HomeIntent
    object ReadLatLongFromDataStore : HomeIntent



}