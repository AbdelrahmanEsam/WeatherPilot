package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.RemoteDataSource
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : Repository {
    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String,
    ): Flow<Response<T>> {

        return try {
            val response = remoteDataSource.getWeatherResponse<T>(longitude, latitude, language)
            if (response.first().error.isNullOrEmpty()) {
                localDataSource.clearWeatherCacheDatabase<T>()
                localDataSource.saveResponseToDatabase(response.first().data as WeatherResponse)
            } else {
                flowOf(Response.Failure(response.first().error ?: "unknownError"))
            }
        } catch (e: Exception) {
            flowOf(Response.Failure("unknown error"))
        }
    }

    override suspend fun <T> getSearchResponse(search: String): Flow<Response<T>> {
        return remoteDataSource.getSearchResponse(search)
    }

    override suspend fun <T> saveStringToDataStore(key: String, value: String): Flow<Response<T>> {
        return localDataSource.saveStringToDataStore(key, value)
    }

    override suspend fun <T> getStringFromDataStore(key: String): Flow<Response<T>> {
        return localDataSource.getStringFromDataStore(key)
    }

    override fun getFavourites(): Flow<List<Location>> {
        return localDataSource.getFavourites()
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
        return localDataSource.insertFavouriteLocation(location)
    }

    override suspend fun <T> deleteFavouriteLocation(id: Int): Flow<Response<T>> {
        return localDataSource.deleteFavouriteLocation(id)
    }

    override suspend fun  getWeatherFromRemoteResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<WeatherModel>> {
       return try {
            remoteDataSource.getWeatherResponse<WeatherResponse>(longitude, latitude, language).map { Response.Success(
                it.data!!.toWeatherModel()) }
        }catch (e:Exception){
            flowOf(Response.Failure(e.message ?: "unknownError"))
        }
    }


    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>> {
        return localDataSource.insertAlertToDatabase(alert)
    }

    override suspend fun <T> deleteAlertFromDatabase(item: SavedAlert): Flow<Response<T>> {
        return localDataSource.deleteAlertFromDatabase(item)
    }

    override fun getAlerts(): Flow<List<AlertItem>> {
        return localDataSource.getAlerts().map { list -> list.map { it.toAlertItem() } }
    }

    override suspend fun <T> updateAlert(alert: SavedAlert): Flow<Response<T>> {
        return localDataSource.updateAlert(alert)
    }


    override suspend fun <T> updateResponseToDatabase(city: String): Flow<Response<T>> {
        return localDataSource.updateResponseToDatabase(city)
    }


    override suspend fun getCachedWeatherFromDatabase(): Flow<List<WeatherModel>> {
        return localDataSource.getCachedWeatherFromDatabase()
            .map { it.map { response -> response.toWeatherModel() } }
    }


}