package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.RemoteDataSource
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : Repository {
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

    override suspend fun <T> saveStringToDataStore(key: String, value: String) : Flow<Response<T>> {
      return localDataSource.saveStringToDataStore(key,value)
    }

    override suspend fun  <T> getStringFromDataStore(key: String): Flow<Response<T>> {
      return  localDataSource.getStringFromDataStore(key)
    }

    override fun getFavourites(): Flow<List<Location>> {
        return localDataSource.getFavourites()
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
      return  localDataSource.insertFavouriteLocation(location)
    }

    override suspend fun <T> deleteFavouriteLocation(longitude: String, latitude: String) : Flow<Response<T>> {
        return localDataSource.deleteFavouriteLocation(longitude, latitude)
    }

    override suspend fun <T>  insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>>  {
        return localDataSource.insertAlertToDatabase(alert)
    }

    override suspend fun <T> deleteAlertFromDatabase(item : SavedAlert) : Flow<Response<T>> {
       return  localDataSource.deleteAlertFromDatabase(item)
    }

    override fun <T> getAlerts(): Flow<Response<T>> {
        return  localDataSource.getAlerts()
    }

    override suspend fun <T> updateAlert(alert: SavedAlert) : Flow<Response<T>> {
       return localDataSource.updateAlert(alert)
    }
}