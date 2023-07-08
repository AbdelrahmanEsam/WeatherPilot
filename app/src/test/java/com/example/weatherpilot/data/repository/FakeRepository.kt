package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException
import java.io.IOException

class FakeRepository(
    private val favourites : MutableList<FavouriteLocation> = mutableListOf()
    , private val alerts : MutableList<SavedAlert> = mutableListOf()
    , private val dataStore : MutableMap<String,String?> = mutableMapOf()
    , private val weatherItems : List<WeatherResponse> = mutableListOf()
    , private val searchItems : SearchResponseDto?= null
) : Repository  {




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




    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<T>> {
        return  flowOf(
            if (shouldReturnGeneralError)  Response.Failure("error") else if(shouldReturnConnectionError) Response.Failure("Please check your network connection") else Response.Success(
                weatherItems.firstOrNull { it.lon == longitude.toDouble() && it.lat == latitude.toDouble()} as T
            )
        )

    }

    override suspend fun <T> getSearchResponse(search: String): Flow<Response<T>> {
        return flowOf(if (shouldReturnGeneralError) Response.Failure("error") else if(shouldReturnConnectionError) Response.Failure("Please check your network connection") else Response.Success(searchItems?.firstOrNull { it.name == search } as T))
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

    override suspend fun <T> deleteFavouriteLocation(longitude: String, latitude: String)  : Flow<Response<T>> {

        return flowOf(if(shouldReturnGeneralError){
            Response.Failure("error")
        }else{
            favourites.removeIf { it.longitude == longitude && it.latitude == latitude }
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

    override fun <T> getAlerts() : Flow<Response<T>>{
        return flowOf( if (shouldReturnGeneralError){
            Response.Failure("error")
        }else{

            Response.Success(alerts as T)
        })
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
}