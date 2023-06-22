package com.example.weatherpilot.data.remote

import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("data/2.5/forecast")
    fun getWeatherResponse(@Query("lat") latitude : String
                        , @Query("lon") longitude :String
                        , @Query("lang") lang : String = "en"
                        ,@Query("units") units : String = "metric"
    ,@Query("appid") apiId : String  = com.example.weatherpilot.BuildConfig.API_KEY) : WeatherResponse


}