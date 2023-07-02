package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class DeleteFavouriteFavouriteUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute(longitude : String , latitude : String)
    {
        repository.deleteFavouriteLocation(longitude, latitude)
    }
}