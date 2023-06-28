package com.example.weatherpilot.domain.repository

import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.util.NetworkResponse
import kotlinx.coroutines.flow.Flow


interface Repository {

    suspend fun <T> getWeatherResponse(longitude: String, latitude: String, language: String)  : Flow<NetworkResponse<T>>

    suspend fun saveStringToDataStore(key : String , value : String)


    suspend fun getStringFromDataStore(key : String) : Flow<String?>

     fun getFavourites() : Flow<List<Location>>

    suspend fun insertFavouriteLocation(location: Location)

    suspend fun deleteFavouriteLocation(location: Location)
}