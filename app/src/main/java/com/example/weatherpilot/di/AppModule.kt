package com.example.weatherpilot.di

import android.content.Context
import androidx.room.Room
import com.example.weatherpilot.BuildConfig
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.data.repository.RepositoryImpl
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.ConnectivityObserver
import com.example.weatherpilot.util.NetworkConnectivityObserver
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferencesImpl
import com.example.weatherpilot.data.local.room.FavouritesDao
import com.example.weatherpilot.data.local.room.WeatherDatabase
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
    fun providesRetrofitApi(): WeatherInterface
    = Retrofit.Builder()
         .baseUrl(BuildConfig.API_BASE).addConverterFactory(
        GsonConverterFactory.create()).build()
        .create(WeatherInterface::class.java)


    @Provides
    @Singleton
    fun provideUserDataStorePreferences(
        @ApplicationContext applicationContext: Context
    ): DataStoreUserPreferences {
        return  DataStoreUserPreferencesImpl(applicationContext)
    }


    @Singleton
    @Provides
    fun providesRepository(remote: WeatherInterface
       ,dataStore : DataStoreUserPreferences,favouritesDao: FavouritesDao): Repository
    = RepositoryImpl(remote,dataStore,favouritesDao)

    @Singleton
    @Provides
    fun providesWeatherDatabase(@ApplicationContext applicationContext: Context): WeatherDatabase {
        return Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java, "weather_database"
        ).build()
    }

    @Singleton
    @Provides
    fun providesFavouriteDao(database: WeatherDatabase) : FavouritesDao
    {
        return  database.favouritesDao
    }
}