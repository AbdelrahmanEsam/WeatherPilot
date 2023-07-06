package com.example.weatherpilot.domain.model

import com.example.weatherpilot.data.dto.LocalNames

data class SearchItem(val country: String?,
                 val lat: Double,
                 val LocalNames: LocalNames?,
                 val lon: Double,
                 val name: String?,
                 val state: String?)