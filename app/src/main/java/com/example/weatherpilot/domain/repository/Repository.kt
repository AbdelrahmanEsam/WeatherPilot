package com.example.weatherpilot.domain.repository

import com.example.weatherpilot.domain.model.WeatherModel

interface Repository {

    suspend fun getWeatherResponse(longitude : String , latitude : String)  : WeatherModel
}