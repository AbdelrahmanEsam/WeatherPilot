package com.example.weatherpilot.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.usecase.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.SaveStringToDataStoreUseCase
import com.example.weatherpilot.util.Dispatcher
import com.example.weatherpilot.util.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase,
    private val readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase
) : ViewModel() {

    private val _state : MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()


    fun onEvent(intent: SettingsIntent)
    {
        when(intent)
        {
            is SettingsIntent.LanguageChange -> {
                saveStringToDataStore("languageType",intent.language)
            }
            is SettingsIntent.LocationChange -> {
                saveStringToDataStore("locationType",intent.location)
            }
            is SettingsIntent.NotificationChange -> {
                saveStringToDataStore("notificationType",intent.notification)
            }
            is SettingsIntent.TemperatureChange ->{
                saveStringToDataStore("temperatureType",intent.temperature)
            }
            is SettingsIntent.WindChange -> {
                saveStringToDataStore("windType",intent.wind)
            }
        }

    }

    private fun saveStringToDataStore(key : String, value : String)
    {
        Log.d("saving", value)
        viewModelScope.launch(ioDispatcher)
        {
               saveStringToDataStoreUseCase.execute(key,value)
        }
    }

    private fun readAllPreferencesFromDataStore()
    {


        SettingsState::class.java.declaredFields.forEach {field ->
        viewModelScope.launch(ioDispatcher)
        {

                val property =  SettingsState::class.java.getDeclaredField(field.name)
                    property.isAccessible = true
                readStringFromDataStoreUseCase.execute(property.name).distinctUntilChanged().collect{
                    val newState = _state.value.copy()
                    property.set(newState,it)
                    _state.update { newState }
                     Log.d("readed", state.value.toString())
                }
            }
        }
    }

    init {
        readAllPreferencesFromDataStore()
    }

}