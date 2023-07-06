package com.example.weatherpilot.domain.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow


interface Repository {

    suspend fun <T> getWeatherResponse(longitude: String, latitude: String, language: String)  : Flow<Response<T>>


    suspend fun <T> getSearchResponse(search : String) : Flow<Response<T>>

    suspend fun saveStringToDataStore(key : String , value : String)


    suspend fun getStringFromDataStore(key : String) : Flow<String?>

     fun getFavourites() : Flow<List<Location>>

    suspend fun <T> insertFavouriteLocation(location: FavouriteLocation) : Flow<Response<T>>

    suspend fun deleteFavouriteLocation(longitude: String,latitude: String)

    suspend fun <T> insertAlertToDatabase(alert : SavedAlert) : Flow<Response<T>>

    suspend fun deleteAlertFromDatabase(item: SavedAlert )

    fun getAlerts() : Flow<List<AlertItem>>


    suspend fun updateAlert(alert: SavedAlert)
}