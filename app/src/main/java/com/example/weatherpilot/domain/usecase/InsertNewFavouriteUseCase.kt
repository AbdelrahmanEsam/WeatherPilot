package com.example.weatherpilot.domain.usecase


import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class InsertNewFavouriteUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute(location : Location)
    {
        repository.insertFavouriteLocation(location = location)
    }

}