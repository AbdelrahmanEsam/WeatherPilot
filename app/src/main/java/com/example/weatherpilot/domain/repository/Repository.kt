package com.example.weatherpilot.domain.repository

import com.example.weatherpilot.domain.model.WeatherModel

interface Repository {

    suspend fun getWeatherResponse(longitude : String , latitude : String)  : WeatherModel

    suspend fun saveStringToDataStore(key : String , value : String)


    suspend fun getStringFromDataStore(key : String) : String
}