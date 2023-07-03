package com.example.weatherpilot.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(alert: SavedAlert)

    @Query("DELETE  FROM Alerts WHERE arabicName = :arabicName and englishName = :englishName and longitude = :longitude and latitude = :latitude")
    suspend  fun delete(arabicName : String ,englishName : String,longitude: String , latitude : String)

    @Query("SELECT * FROM Alerts")
     fun  getAllAlerts() : Flow<List<SavedAlert>>
}