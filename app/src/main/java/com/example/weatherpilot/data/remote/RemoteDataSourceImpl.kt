package com.example.weatherpilot.data.remote

import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val remote: WeatherInterface,) : RemoteDataSource {
    override suspend fun <T> getWeatherResponse(
        longitude: String,
        latitude: String,
        language: String
    ): Flow<Response<T>> {
        return try {
            flowOf(
                Response.Success(
                    remote.getWeatherResponse(
                        longitude = longitude,
                        latitude = latitude,
                        lang = language
                    ).toWeatherModel() as T
                )
            )
        } catch (e: Exception) {
            flowOf(Response.Failure(e.message ?: "error"))
        } catch (e: HttpException) {
            flowOf(Response.Failure(e.message ?: "Something went wrong"))
        } catch (e: IOException) {
            flowOf(Response.Failure("Please check your network connection"))
        } catch (e: Exception) {
            flowOf(Response.Failure("Something went wrong"))
        }
    }

    override suspend fun <T> getSearchResponse(search: String): Flow<Response<T>> {
        return try {
            flowOf(
                Response.Success(
                    remote.searchCityByName(
                        search
                    ) as T
                )
            )
        } catch (e: Exception) {
            flowOf(Response.Failure(e.message ?: "error"))
        } catch (e: HttpException) {
            flowOf(Response.Failure(e.message ?: "Please check your network connection"))
        } catch (e: IOException) {
            flowOf(Response.Failure("Please check your network connection"))
        }
    }
}