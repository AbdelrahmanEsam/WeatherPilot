package com.example.weatherpilot.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(alert: SavedAlert)

    @Query("DELETE  FROM Alerts WHERE id = :id")
    suspend  fun delete(id : Int )

    @Update
    suspend fun update(alert: SavedAlert)

    @Query("SELECT * FROM Alerts")
     fun  getAllAlerts() : Flow<List<SavedAlert>>
}