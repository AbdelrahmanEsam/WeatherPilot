package com.example.weatherpilot.presentation.favourites

import com.example.weatherpilot.domain.model.Location

sealed interface FavouritesIntent
{
    object  FetchFavouritesFromDatabase : FavouritesIntent

    data class DeleteItem(val location: Location) : FavouritesIntent


}