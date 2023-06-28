package com.example.weatherpilot.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherpilot.data.dto.FavouriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(location: FavouriteLocation)

    @Delete
    suspend  fun delete(location: FavouriteLocation)

    @Query("SELECT * FROM Favourites")
     fun  getAllProducts() : Flow<List<FavouriteLocation>>
}