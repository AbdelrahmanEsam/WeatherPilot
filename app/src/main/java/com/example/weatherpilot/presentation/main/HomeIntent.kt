package com.example.weatherpilot.presentation.main

sealed interface HomeIntent
{
    data class NewLocation(val longitude : String , val latitude  :  String) : HomeIntent

}