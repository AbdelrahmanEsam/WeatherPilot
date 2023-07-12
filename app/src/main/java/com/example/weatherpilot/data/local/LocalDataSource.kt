package com.example.weatherpilot.data.local

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {


    fun getFavourites() : Flow<List<Location>>

    suspend fun <T> insertFavouriteLocation(location: FavouriteLocation) : Flow<Response<T>>

    suspend fun <T> deleteFavouriteLocation(id : Int) : Flow<Response<T>>

    suspend fun <T> insertAlertToDatabase(alert : SavedAlert)  : Flow<Response<T>>

    suspend fun <T> deleteAlertFromDatabase(item : SavedAlert ) : Flow<Response<T>>

    fun getAlerts() : Flow<List<SavedAlert>>

    suspend fun <T> updateAlert(alert: SavedAlert) : Flow<Response<T>>


    suspend fun <T> saveStringToDataStore(key : String , value : String) : Flow<Response<T>>


    suspend fun <T> getStringFromDataStore(key : String) : Flow<Response<T>>


    suspend fun <T> saveResponseToDatabase(response : WeatherResponse) : Flow<Response<T>>


    suspend fun <T> updateResponseToDatabase(city: String)  : Flow<Response<T>>


    suspend fun <T> deleteResponseFromDatabase(lat : Double , long : Double) : Flow<Response<T>>


    suspend fun <T> clearWeatherCacheDatabase() : Flow<Response<T>>


    suspend fun getCachedWeatherFromDatabase() :  Flow<List<WeatherResponse>>
}