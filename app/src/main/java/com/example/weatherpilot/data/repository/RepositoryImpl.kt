package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.data.local.room.FavouritesDao
import com.example.weatherpilot.data.local.room.LocalDataSource
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remote: WeatherInterface,
    private val dataStore: DataStoreUserPreferences,
    private val localDataSource: LocalDataSource,
) : Repository {
    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<T>> {
        return try {
            flowOf(
                Response.Success(
                    remote.getWeatherResponse(
                        longitude = longitude,
                        latitude = latitude,
                        lang = language
                    ).toWeatherModel() as T
                )
            )
        } catch (e: Exception) {
            flowOf(Response.Failure(e.message ?: "error"))
        } catch (e: HttpException) {
            flowOf(Response.Failure(e.message ?: "Something went wrong"))
        } catch (e: IOException) {
            flowOf(Response.Failure("Please check your network connection"))
        } catch (e: Exception) {
            flowOf(Response.Failure("Something went wrong"))
        }
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
        dataStore.putString(key, value)
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
        return dataStore.getString(key)
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
        localDataSource.deleteAlertFromDatabase(item)
    }

    override fun getAlerts(): Flow<List<AlertItem>> {
        return  localDataSource.getAlerts().map { it.map { savedAlert -> savedAlert.toAlertItem() } }
    }
}