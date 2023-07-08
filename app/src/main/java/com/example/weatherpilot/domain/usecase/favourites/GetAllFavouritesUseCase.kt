package com.example.weatherpilot.domain.usecase.favourites

import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFavouritesUseCase @Inject constructor(private val repository: Repository) {

    fun execute() : Flow<List<Location>>
    {
        return repository.getFavourites()
    }
}