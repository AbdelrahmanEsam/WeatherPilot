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

    override suspend fun saveStringToDataStore(key: String, value: String) {
      localDataSource.saveStringToDataStore(key,value)
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
      return  localDataSource.getStringFromDataStore(key)
    }

    override fun getFavourites(): Flow<List<Location>> {
        return localDataSource.getFavourites()
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
      return  localDataSource.insertFavouriteLocation(location)
    }

    override suspend fun deleteFavouriteLocation(longitude: String, latitude: String) {
        return localDataSource.deleteFavouriteLocation(longitude, latitude)
    }

    override suspend fun <T>  insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>>  {
        return localDataSource.insertAlertToDatabase(alert)
    }

    override suspend fun deleteAlertFromDatabase(item : SavedAlert) {
        try {


        localDataSource.deleteAlertFromDatabase(item)
        }catch (e : Exception){
            throw  e
        }
    }

    override fun getAlerts(): Flow<List<AlertItem>> {
        return  localDataSource.getAlerts().map { it.map { savedAlert -> savedAlert.toAlertItem() } }
    }

    override suspend fun updateAlert(alert: SavedAlert) {
       localDataSource.updateAlert(alert)
    }
}