package com.example.weatherpilot.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherpilot.data.dto.FavouriteLocation

@Database(
    entities = [FavouriteLocation::class],
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val favouritesDao: FavouritesDao

}