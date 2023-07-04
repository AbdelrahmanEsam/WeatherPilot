package com.example.weatherpilot.data.local.room

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val favouritesDao: FavouritesDao,
    private val alertsDao: AlertsDao
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

    override suspend fun deleteFavouriteLocation(longitude: String, latitude: String) {
        favouritesDao.delete(longitude, latitude)
    }

    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert)  : Flow<Response<T>>{
        return try{
       alertsDao.insert(alert)

        flowOf(Response.Success("successful insert" as T))

    } catch (e: Exception) {
        flowOf(Response.Failure(e.message ?: "Something went wrong"))
    }
    }

    override suspend fun deleteAlertFromDatabase(item : SavedAlert) {
        alertsDao.delete(item.id)
    }

    override fun getAlerts(): Flow<List<SavedAlert>> {
        return alertsDao.getAllAlerts()
    }

    override suspend fun updateAlert(alert: SavedAlert) {
        alertsDao.update(alert)
    }


}