package com.example.weatherpilot.data.dto

data class SearchResponseItem(
    val country: String?,
    val lat: Double,
    val local_names: LocalNames?,
    val lon: Double,
    val name: String?,
    val state: String?
)