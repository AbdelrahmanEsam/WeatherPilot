package com.example.weatherpilot.data.local.room

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {


    fun getFavourites() : Flow<List<Location>>

    suspend fun <T> insertFavouriteLocation(location: FavouriteLocation) : Flow<Response<T>>

    suspend fun deleteFavouriteLocation(longitude: String,latitude: String)

    suspend fun <T> insertAlertToDatabase(alert : SavedAlert)  : Flow<Response<T>>

    suspend fun deleteAlertFromDatabase(item : SavedAlert )

    fun getAlerts() : Flow<List<SavedAlert>>

    suspend fun updateAlert(alert: SavedAlert)
}