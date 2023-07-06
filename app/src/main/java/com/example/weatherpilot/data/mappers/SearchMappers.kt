package com.example.weatherpilot.data.mappers

import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.SearchResponseItem
import com.example.weatherpilot.domain.model.SearchItem
import com.example.weatherpilot.domain.model.SearchResponse


fun SearchResponseDto.toSearchResponse() : SearchResponse
{
    return SearchResponse(this.map { it.toSearchItem() })
}
fun SearchResponseItem.toSearchItem() : SearchItem
{
    return SearchItem(country,lat,local_names,lon, name, state)
}