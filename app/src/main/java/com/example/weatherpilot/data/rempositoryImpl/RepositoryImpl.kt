package com.example.weatherpilot.data.rempositoryImpl

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.data.local.room.FavouritesDao
import com.example.weatherpilot.data.mappers.toFavouriteLocation
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remote: WeatherInterface,
    private val dataStore: DataStoreUserPreferences,
    private val favouritesDao: FavouritesDao
) : Repository {
    override suspend fun  <T> getWeatherResponse(longitude: String, latitude: String, language: String): Flow<NetworkResponse<T>> {

        return  try {
             flowOf( NetworkResponse.Success(
                remote.getWeatherResponse(
                    longitude = longitude,
                    latitude = latitude,
                    lang = language
                ).toWeatherModel() as T
            ))
        }catch (e : Exception){
            flowOf(NetworkResponse.Failure(e.message ?: "error"))
        }
        catch (e: HttpException) {
            flowOf(NetworkResponse.Failure( e.message ?: "Something went wrong"))
        } catch (e: IOException) {
            flowOf(NetworkResponse.Failure("Please check your network connection"))
        } catch (e: Exception) {
            flowOf(NetworkResponse.Failure("Something went wrong"))
        }
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
           dataStore.putString(key, value)
    }

    override suspend fun getStringFromDataStore(key: String): Flow<String?> {
        return   dataStore.getString(key)
    }

    override fun getFavourites(): Flow<List<Location>> {
        return favouritesDao.getAllProducts().map { it.map(FavouriteLocation::toLocation) }
    }

    override suspend fun insertFavouriteLocation(location: Location) {
        favouritesDao.insert(location.toFavouriteLocation())
    }

    override suspend fun deleteFavouriteLocation(longitude: String,latitude : String) {
        favouritesDao.delete(longitude, latitude)
    }
}