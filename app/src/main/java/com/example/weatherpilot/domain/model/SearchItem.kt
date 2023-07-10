package com.example.weatherpilot.domain.model

import com.example.weatherpilot.data.dto.LocalNames

data class SearchItem(val country: String? = null,
                 val lat: Double = 0.0,
                 val LocalNames: LocalNames? = null,
                 val lon: Double = 0.0,
                 val name: String?,
                 val state: String? = null)