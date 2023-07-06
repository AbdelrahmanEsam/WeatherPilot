package com.example.weatherpilot.data.remote

import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("/data/2.5/onecall")
   suspend fun getWeatherResponse(
                        @Query("lat") latitude : String
                        ,@Query("lon") longitude :String
                        ,@Query("lang") lang : String = "en"
                        ,@Query("units") units : String = "metric"
                        ,@Query("cnt") hours : String = "24"
                        ,@Query("exclude") exclude : String = "minutely,alerts"
    ,@Query("appid") apiId : String  = com.example.weatherpilot.BuildConfig.API_KEY) : WeatherResponse


   @GET("geo/1.0/direct")
   suspend fun searchCityByName(@Query("q") cityName : String,@Query("limit") limit : Int = 20,@Query("appid") apiId : String  = com.example.weatherpilot.BuildConfig.API_KEY) :SearchResponseDto

}