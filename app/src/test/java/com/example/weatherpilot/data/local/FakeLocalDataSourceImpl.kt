package com.example.weatherpilot.data.local

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSourceImpl (private val favourites : MutableList<FavouriteLocation>, private val alerts : MutableList<SavedAlert>,private val dataStore : MutableMap<String,String?> ): LocalDataSource {


    private var shouldReturnGeneralError = false

    private var shouldReturnConnectionError = false

    fun setShouldReturnGeneralError(should : Boolean)
    {
        shouldReturnGeneralError = should
    }


    fun setShouldReturnConnectionError(should : Boolean)
    {
        shouldReturnConnectionError = should
    }



    override fun getFavourites(): Flow<List<Location>> {
        return flow { emit(favourites.map(FavouriteLocation::toLocation)) }
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
        favourites.add(location)
       return flowOf(if (shouldReturnGeneralError) Response.Failure("error") else Response.Success("success" as T))
    }

    override suspend fun deleteFavouriteLocation(longitude: String, latitude: String) {
        favourites.removeIf { it.longitude == longitude && it.latitude == latitude }
    }

    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>> {
        alerts.add(alert)
        return  flowOf(if (shouldReturnGeneralError) Response.Failure("error") else Response.Success("success" as T))
    }

    override suspend fun deleteAlertFromDatabase(item: SavedAlert) {
        alerts.remove(item)
    }

    override fun getAlerts(): Flow<List<SavedAlert>> {
        return  flowOf(alerts)
    }

    override suspend fun updateAlert(alert: SavedAlert) {
        alerts.removeIf { it.time == alert.time }
        alerts.add(alert)
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
        dataStore[key] = value
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
        return flowOf(if (dataStore[key].isNullOrBlank()) null else dataStore[key])
    }
}