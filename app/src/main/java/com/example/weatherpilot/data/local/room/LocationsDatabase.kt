package com.example.weatherpilot.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.WeatherResponse

@Database(
    entities = [FavouriteLocation::class,SavedAlert::class,WeatherResponse::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val favouritesDao: FavouritesDao


    abstract val alertsDao : AlertsDao

    abstract val weatherCacheDao :WeatherCacheDao
}