package com.example.weatherpilot.presentation.main

import com.example.weatherpilot.domain.model.DayWeatherModel
import com.example.weatherpilot.domain.model.HourWeatherModel

data class HomeState(val city : String? = null
, val weatherState : String? = null
, val iconCode : String? = null
, val temp : String? = null
, val date  : String? = null
, val pressure : String? = null
, val humidity : String? = null
, val wind : String? = null
, val visibility : String? = null
, val clouds : String? = null
, val dayState : List<HourWeatherModel> = listOf()
, val weekState : List<DayWeatherModel> = listOf()
)
