package com.example.weatherpilot.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.weatherpilot.BuildConfig
import com.example.weatherpilot.R
import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.data.repository.RepositoryImpl
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.connectivity.ConnectivityObserver
import com.example.weatherpilot.util.connectivity.NetworkConnectivityObserver
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferencesImpl
import com.example.weatherpilot.data.local.room.AlertsDao
import com.example.weatherpilot.data.local.room.FavouritesDao
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.local.LocalDataSourceImpl
import com.example.weatherpilot.data.local.room.WeatherCacheDao
import com.example.weatherpilot.data.local.room.WeatherDatabase
import com.example.weatherpilot.data.remote.RemoteDataSource
import com.example.weatherpilot.data.remote.RemoteDataSourceImpl
import com.example.weatherpilot.util.alarm.AlarmScheduler
import com.example.weatherpilot.util.alarm.AlarmSchedulerInterface
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
    fun providesLocationManager(@ApplicationContext context: Context) : LocationManager
    {
        return context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
    }






    @Singleton
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context) : NotificationManager
    {
        return context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
    }



    @Singleton
    @Provides
    fun providesAlarmManager(@ApplicationContext context: Context) : AlarmManager
    {
        return context.getSystemService(AlarmManager::class.java)
    }


    @Singleton
    @Provides
    fun providesAlarmScheduler(@ApplicationContext context: Context,alarmManager: AlarmManager) : AlarmSchedulerInterface
    {
        return AlarmScheduler(context,alarmManager)
    }


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


    @Provides
    @Singleton
    fun providesLocalDataSource(alertsDao: AlertsDao,weatherCacheDao: WeatherCacheDao,favouritesDao: FavouritesDao ,dataStore : DataStoreUserPreferences) : LocalDataSource
    {
        return LocalDataSourceImpl(favouritesDao, alertsDao,weatherCacheDao,dataStore)
    }


    @Provides
    @Singleton
    fun providesRemoteDataSource(remote: WeatherInterface) : RemoteDataSource
    {
        return RemoteDataSourceImpl(remote)
    }


    @Singleton
    @Provides
    fun providesRepository(remote: RemoteDataSource
      ,localDataSource: LocalDataSource,
      connectivityObserver: ConnectivityObserver
    ): Repository
    = RepositoryImpl(remote,localDataSource,connectivityObserver)

    @Singleton
    @Provides
    fun providesWeatherDatabase(@ApplicationContext applicationContext: Context): WeatherDatabase {
        return Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java, applicationContext.getString(R.string.weather_database)
        ).build()
    }

    @Singleton
    @Provides
    fun providesFavouriteDao(database: WeatherDatabase) : FavouritesDao
    {
        return  database.favouritesDao
    }

    @Singleton
    @Provides
    fun providesAlertsDao(database: WeatherDatabase) : AlertsDao
    {
        return  database.alertsDao
    }


    @Singleton
    @Provides
    fun providesWeatherCacheDao(database: WeatherDatabase) : WeatherCacheDao
    {
        return database.weatherCacheDao
    }
}