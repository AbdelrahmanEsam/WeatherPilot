package com.example.weatherpilot.data.rempositoryImpl

import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val remote : WeatherInterface, private  val dataStore : DataStoreUserPreferences) : Repository {
    override suspend fun getWeatherResponse(longitude : String , latitude : String): WeatherModel {
        return remote.getWeatherResponse(longitude = longitude, latitude =  latitude).toWeatherModel()
    }

    override suspend fun saveStringToDataStore(key: String, value: String) {
           dataStore.putString(key, value)
    }

    override suspend fun getStringFromDataStore(key: String): String {
        return dataStore.getString(key).toString()
    }
}