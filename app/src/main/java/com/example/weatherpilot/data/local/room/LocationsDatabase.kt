package com.example.weatherpilot.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert

@Database(
    entities = [FavouriteLocation::class,SavedAlert::class],
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val favouritesDao: FavouritesDao


    abstract val alertsDao : AlertsDao

}