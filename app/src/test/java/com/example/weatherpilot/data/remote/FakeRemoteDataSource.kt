package com.example.weatherpilot.data.remote

import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException
import java.io.IOException

class FakeRemoteDataSource( private val weatherItems : List<WeatherResponse>,private val searchItems : SearchResponseDto) : RemoteDataSource {



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
                if (shouldReturnGeneralError)  Response.Failure("unknown error") else if(shouldReturnConnectionError) Response.Failure("Please check your network connection") else Response.Success(
                        weatherItems.firstOrNull { it.lon == longitude.toDouble() && it.lat == latitude.toDouble()} as T
                        )
        )

    }

    override suspend fun <T> getSearchResponse(search: String): Flow<Response<T>> {
        return flowOf(if (shouldReturnGeneralError) Response.Failure("unknown error") else if(shouldReturnConnectionError) Response.Failure("Please check your network connection") else Response.Success(searchItems.firstOrNull { it.name == search } as T))
        }
    }


