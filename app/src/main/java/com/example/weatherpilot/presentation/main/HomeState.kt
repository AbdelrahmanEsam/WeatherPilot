package com.example.weatherpilot.presentation.main

import com.example.weatherpilot.domain.model.DayWeatherModel
import com.example.weatherpilot.domain.model.HourWeatherModel




sealed interface HomeState
{
    data class Display(val city : String? = null
                           , val weatherState : String? = null
                           , val iconCode : String? = null
                           , val temp : String? = null
                           , val date  : String? = null
                           , val pressure : String? = null
                           , val humidity : String? = null
                           , val wind : String? = null
                           , val visibility : String? = null
                           , val clouds : String? = null
    ,val dayState : List<HourWeatherModel> = listOf(),
    val weekState : List<DayWeatherModel> = listOf(),
    val loading :Boolean = false,
    val error : String? = null
    ) : HomeState

    data class Preferences(val locationType : String? = null,
    val  languageType : String? = "english",
    val windType : String? = null,
    val temperatureType : String? = null) : HomeState


    data class LongLat( val latitude : String? = null,
                val longitude : String? = null) : HomeState


}
