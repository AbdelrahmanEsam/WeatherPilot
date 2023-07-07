package com.example.weatherpilot.data.dto

data class SearchResponseItem(
    val country: String? = "",
    val local_names: LocalNames? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val name: String? = "",
    val state: String? = ""
)