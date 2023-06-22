package com.example.weatherpilot.di

import android.content.Context
import com.example.weatherpilot.BuildConfig
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.util.ConnectivityObserver
import com.example.weatherpilot.util.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule
{

    @Singleton
    @Provides
    fun providesConnectivityObserver(@ApplicationContext context: Context) : ConnectivityObserver
    = NetworkConnectivityObserver(context = context)



     @Singleton
     @Provides
    fun providesRetrofitApi(): WeatherInterface = Retrofit.Builder().baseUrl(BuildConfig.API_BASE).addConverterFactory(
        GsonConverterFactory.create()).build()
        .create(WeatherInterface::class.java)
}