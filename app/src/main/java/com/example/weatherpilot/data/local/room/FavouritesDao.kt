package com.example.weatherpilot.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherpilot.data.dto.FavouriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(location: FavouriteLocation)

    @Query("DELETE FROM Favourites WHERE id = :id")
    suspend  fun delete(id : Int)

    @Query("SELECT * FROM Favourites")
     fun  getAllFavourites() : Flow<List<FavouriteLocation>>
}