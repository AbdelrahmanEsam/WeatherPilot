package com.example.weatherpilot.domain.repository

interface Repository {

    suspend fun getApiResponse()
}