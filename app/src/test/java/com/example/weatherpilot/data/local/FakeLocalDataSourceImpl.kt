package com.example.weatherpilot.data.local

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSourceImpl (private val favourites : MutableList<FavouriteLocation>, private val alerts : MutableList<SavedAlert>,private val cachedResponse : MutableList<WeatherResponse>,private val dataStore : MutableMap<String,String?> ): LocalDataSource {


    private var shouldReturnGeneralError = false


    fun setShouldReturnGeneralError(should : Boolean)
    {
        shouldReturnGeneralError = should
    }








    override fun getFavourites(): Flow<List<Location>> {
        return flow { emit(favourites.map(FavouriteLocation::toLocation)) }
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
        return flowOf(if(shouldReturnGeneralError){
            Response.Failure("error")
        }else{
            favourites.add(location)
            Response.Success("success" as T)
        } )
    }

    override suspend fun <T> deleteFavouriteLocation(id : Int)  : Flow<Response<T>> {

        return flowOf(if(shouldReturnGeneralError){
            Response.Failure("error")
        }else{
            favourites.removeIf { it.id == id }
            Response.Success("success" as T)
        } )
    }

    override suspend fun <T> insertAlertToDatabase(alert: SavedAlert): Flow<Response<T>> {

        return flowOf(if(shouldReturnGeneralError){
            Response.Failure("error")
        }else{
            alerts.add(alert)
            Response.Success("success" as T)
        } )

    }

    override suspend fun  <T> deleteAlertFromDatabase(item: SavedAlert)  : Flow<Response<T>>{
        return flowOf( if (shouldReturnGeneralError){
          Response.Failure("error")
        }else{
            alerts.remove(item)
          Response.Success("success" as T)
        })
    }

    override fun  getAlerts(): Flow<List<SavedAlert>> {
         return  flowOf(alerts)
    }

    override suspend fun <T> updateAlert(alert: SavedAlert) : Flow<Response<T>> {
        return flowOf(if(shouldReturnGeneralError){
            Response.Failure("error")
        }else{
            alerts.removeIf { it.time == alert.time }
            alerts.add(alert)
            Response.Success("success" as T)
        } )

    }

    override suspend fun <T> saveStringToDataStore(key: String, value: String) : Flow<Response<T>> {
        dataStore[key] = value
        return flowOf(if (shouldReturnGeneralError) Response.Failure("error")  else Response.Success("success" as T))
    }

    override suspend fun <T> getStringFromDataStore(key: String):  Flow<Response<T>> {
       val value = dataStore[key]
        return flowOf(if (value.isNullOrBlank()) Response.Failure("error") else Response.Success(value as T))
    }

    override suspend fun <T> saveResponseToDatabase(response: WeatherResponse): Flow<Response<T>> {
       cachedResponse.add(response)
        return flowOf(if (shouldReturnGeneralError) Response.Failure("error")  else Response.Success("success" as T))
    }

    override suspend fun <T> updateResponseToDatabase(city: String): Flow<Response<T>> {
        return try {
            val response = cachedResponse.first()
            cachedResponse.clear()
            cachedResponse.add(response.copy(timezone = "any/$city"))
            flowOf(Response.Success("successful insert" as T))
        } catch (e: Exception) {
            flowOf(Response.Failure(e.message ?: "unknown error"))
        }
    }


    override suspend fun <T> clearWeatherCacheDatabase(): Flow<Response<T>> {
       cachedResponse.clear()
        return flowOf(if (shouldReturnGeneralError) Response.Failure("error")  else Response.Success("success" as T))
    }

    override suspend fun getCachedWeatherFromDatabase(): Flow<List<WeatherResponse>> {
        return flowOf(cachedResponse)
    }
}