package com.example.weatherpilot.data.remote

import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {


    suspend fun <T> getWeatherResponse(longitude: String, latitude: String, language: String)  : Flow<Response<T>>


    suspend fun <T> getSearchResponse(search : String) : Flow<Response<T>>
}