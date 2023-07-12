package com.example.weatherpilot.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherpilot.data.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(response: WeatherResponse)

    @Query("DELETE  FROM WeatherResponse WHERE lat = :lat & lon = :lon")
    suspend  fun delete(lat : Double , lon : Double)

    @Update
    suspend fun update(response: WeatherResponse)

    @Query("SELECT * FROM WeatherResponse")
     fun getCachedWeather() : Flow<List<WeatherResponse>>

    @Query("DELETE FROM WeatherResponse")
    fun clearTable()

}