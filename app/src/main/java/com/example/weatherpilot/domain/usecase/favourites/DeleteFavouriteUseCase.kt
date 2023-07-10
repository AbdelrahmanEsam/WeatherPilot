package com.example.weatherpilot.domain.usecase.favourites

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFavouriteUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute(id : Int)  : Flow<Response<String>>
    {
       return repository.deleteFavouriteLocation(id)
    }
}