package com.example.weatherpilot.domain.usecase

import android.util.Log
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class ReadStringFromDataStoreUseCase @Inject constructor(private val repository : Repository) {

    suspend fun execute(key : String) : String
    {
      val data =  repository.getStringFromDataStore(key)
        Log.d("data",data.toString())
        return  data
    }

}