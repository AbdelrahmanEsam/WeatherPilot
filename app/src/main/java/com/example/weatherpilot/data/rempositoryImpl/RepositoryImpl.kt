package com.example.weatherpilot.data.rempositoryImpl

import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val remote : WeatherInterface) : Repository {
    override suspend fun getWeatherResponse(longitude : String , latitude : String): WeatherModel {

        return remote.getWeatherResponse(longitude = longitude, latitude =  latitude).toWeatherModel()
    }
}