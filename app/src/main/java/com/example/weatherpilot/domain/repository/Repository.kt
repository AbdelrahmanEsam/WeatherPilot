package com.example.weatherpilot.domain.repository

import com.example.weatherpilot.domain.model.WeatherModel
import kotlinx.coroutines.flow.Flow


interface Repository {

    suspend fun getWeatherResponse(longitude: String, latitude: String, language: String)  : WeatherModel

    suspend fun saveStringToDataStore(key : String , value : String)


    suspend fun getStringFromDataStore(key : String) : Flow<String?>
}