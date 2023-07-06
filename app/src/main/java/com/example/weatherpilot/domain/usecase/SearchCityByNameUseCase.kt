package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.model.SearchItem
import com.example.weatherpilot.domain.model.SearchResponse
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCityByNameUseCase @Inject constructor(private val repository: Repository) {


  suspend  fun   execute(searchCityName : String) : Flow<Response<SearchResponse>>
    {
        return  repository.getSearchResponse(searchCityName)
    }
}