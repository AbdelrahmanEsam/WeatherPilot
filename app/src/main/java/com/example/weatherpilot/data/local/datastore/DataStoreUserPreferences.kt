package com.example.weatherpilot.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreUserPreferences {

    suspend fun putString(key : String , value : String)
    suspend fun putBoolean(key : String , value: Boolean)
    suspend fun getString (key : String) : Flow<String?>
    suspend fun getBoolean(key : String) : Boolean?


}