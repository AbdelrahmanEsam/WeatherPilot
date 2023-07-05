package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : Repository  {

    private val favourites : MutableList<FavouriteLocation> = mutableListOf()
    private val alerts : MutableList<SavedAlert> = mutableListOf()


    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
        TODO("Not yet implemented")
    }

    override fun getFavourites(): Flow<List<Location>> {
      return flow { emit(favourites.map(FavouriteLocation::toLocation)) }
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
            favourites.add(location)
        return  flowOf(Response.Success("success" as T))
    }


    override suspend fun deleteFavouriteLocation(longitude: String, latitude: String) {
       favourites.removeIf { it.longitude == longitude && it.latitude == latitude }
    }

    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>> {
        alerts.add(alert)
        return  flowOf(Response.Success("success" as T))
    }

    override suspend fun deleteAlertFromDatabase(item: SavedAlert) {
        alerts.remove(item)
    }

    override fun getAlerts(): Flow<List<AlertItem>> {
    return  flowOf(alerts.map(SavedAlert::toAlertItem))
    }

    override suspend fun updateAlert(alert: SavedAlert) {
       alerts.removeIf { it.time == alert.time }
        alerts.add(alert)
    }


}