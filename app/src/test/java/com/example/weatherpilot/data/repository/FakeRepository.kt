package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : Repository  {

    private val favourites : MutableList<FavouriteLocation> = mutableListOf()


    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<T>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
        TODO("Not yet implemented")
    }

    override fun getFavourites(): Flow<List<Location>> {
      return flow { emit(favourites.map(FavouriteLocation::toLocation)) }
    }

    override suspend fun <T> insertFavouriteLocation(location: FavouriteLocation): Flow<Response<T>> {
            favourites.add(location)
        return  flowOf(Response.Success("success" as T))
    }


    override suspend fun deleteFavouriteLocation(longitude: String, latitude: String) {
       favourites.removeIf { it.longitude == longitude && it.latitude == latitude }
    }


}