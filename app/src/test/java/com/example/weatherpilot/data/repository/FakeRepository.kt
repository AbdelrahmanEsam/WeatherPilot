package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.data.remote.RemoteDataSource
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class FakeRepository(private val localDataSource: LocalDataSource,private val remoteDataSource: RemoteDataSource) : Repository  {








    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<T>> {
        return remoteDataSource.getWeatherResponse(longitude, latitude, language)
    }

    override suspend fun <T> getSearchResponse(search: String): Flow<Response<T>> {
        return remoteDataSource.getSearchResponse(search)
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
      localDataSource.saveStringToDataStore(key, value)
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
       return  localDataSource.getStringFromDataStore(key)
    }

    override fun getFavourites(): Flow<List<Location>> {
      return localDataSource.getFavourites()
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
      return localDataSource.insertFavouriteLocation(location)
    }


    override suspend fun deleteFavouriteLocation(longitude: String, latitude: String) {
        localDataSource.deleteFavouriteLocation(longitude, latitude)
    }

    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>> {
    return  localDataSource.insertAlertToDatabase(alert)
    }

    override suspend fun deleteAlertFromDatabase(item: SavedAlert) {
      localDataSource.deleteAlertFromDatabase(item)
    }

    override fun getAlerts(): Flow<List<AlertItem>> {
    return  localDataSource.getAlerts().map { list -> list.map { it.toAlertItem() } }
    }

    override suspend fun updateAlert(alert: SavedAlert) {
       localDataSource.updateAlert(alert)
    }


}