package com.example.weatherpilot.domain.usecase


import com.example.weatherpilot.data.mappers.toFavouriteLocation
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertNewFavouriteUseCase @Inject constructor(private val repository: Repository) {

    suspend fun  execute(location : Location) : Flow<Response<String>>
    {
        return  repository.insertFavouriteLocation(location = location.toFavouriteLocation())
    }

}