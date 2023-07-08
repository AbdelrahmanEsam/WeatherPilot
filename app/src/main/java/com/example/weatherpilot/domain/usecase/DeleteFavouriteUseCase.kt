package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFavouriteUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute(latitude : String,longitude : String)  : Flow<Response<String>>
    {
       return repository.deleteFavouriteLocation(longitude, latitude)
    }
}