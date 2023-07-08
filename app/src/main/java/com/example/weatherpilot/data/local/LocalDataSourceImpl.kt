package com.example.weatherpilot.data.local

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.data.local.room.AlertsDao
import com.example.weatherpilot.data.local.room.FavouritesDao
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val favouritesDao: FavouritesDao,
    private val alertsDao: AlertsDao,
    private val dataStore: DataStoreUserPreferences,
) : LocalDataSource {


    override fun getFavourites(): Flow<List<Location>> {
        return favouritesDao.getAllFavourites().map { it.map(FavouriteLocation::toLocation) }
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
        return try {
            favouritesDao.insert(location)
            flowOf(Response.Success("successful insert" as T))

        } catch (e: Exception) {
            flowOf(Response.Failure(e.message ?: "Something went wrong"))
        }
    }

    override suspend fun <T> deleteFavouriteLocation(longitude: String, latitude: String) : Flow<Response<T>> {
        return try {
            favouritesDao.delete(longitude, latitude)
            flowOf(Response.Success("successful insert" as T))
        }catch (e : Exception) {
            flowOf(Response.Failure(e.message ?: "unknown error"))
        }
    }

    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert)  : Flow<Response<T>>{
        return try{
       alertsDao.insert(alert)

        flowOf(Response.Success("successful insert" as T))

    } catch (e: Exception) {
        flowOf(Response.Failure(e.message ?: "Something went wrong"))
    }
    }

    override suspend fun <T> deleteAlertFromDatabase(item : SavedAlert) : Flow<Response<T>> {
        return flowOf(try {
            alertsDao.delete(item.id)
            Response.Success("successful insert" as T)
        }catch (e : Exception){
            Response.Failure(e.message ?: "unknown error")
        })
    }

    override fun <T>getAlerts():  Flow<Response<T>> {

        return flowOf(try {
            Response.Success(alertsDao.getAllAlerts().map { savedAlerts-> savedAlerts.map { it.toAlertItem() } }  as T)
        }catch (e : Exception){
            Response.Failure(e.message ?: "unknown error")
        })

    }

    override suspend fun <T> updateAlert(alert: SavedAlert) : Flow<Response<T>> {

        return try {
            alertsDao.update(alert)
            flowOf(Response.Success("successful insert" as T))
        }catch (e : Exception) {
            flowOf(Response.Failure(e.message ?: "unknown error"))
        }
    }

    override suspend fun <T> saveStringToDataStore(key: String, value: String) : Flow<Response<T>>{


        return try {
            dataStore.putString(key, value)
            flowOf(Response.Success("successful insert" as T))
        }catch (e : Exception) {
            flowOf(Response.Failure(e.message ?: "unknown error"))
        }
    }

    override suspend fun <T> getStringFromDataStore(key: String): Flow<Response<T>> {
        return try {
          return  dataStore.getString(key)
        }catch (e : Exception) {
            flowOf(Response.Failure(e.message ?: "unknown error"))
        }
    }


}