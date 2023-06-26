package com.example.weatherpilot.presentation.settings

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
                _state.update { it.copy(language = intent.language) }
                saveStringToDataStore("language",intent.language)
            }
            is SettingsIntent.LocationChange -> {
                _state.update { it.copy(location = intent.location) }
                saveStringToDataStore("location",intent.location)
            }
            is SettingsIntent.NotificationChange -> {
                _state.update { it.copy(language = intent.notification) }
                saveStringToDataStore("notification",intent.notification)
            }
            is SettingsIntent.TemperatureChange ->{
                _state.update { it.copy(language = intent.temperature) }
                saveStringToDataStore("temperature",intent.temperature)
            }
            is SettingsIntent.WindChange -> {
                _state.update { it.copy(language = intent.wind) }
                saveStringToDataStore("wind",intent.wind)
            }
        }

    }

    private fun saveStringToDataStore(key : String, value : String)
    {
        viewModelScope.launch(ioDispatcher)
        {
               saveStringToDataStoreUseCase.execute(key,value)
        }
    }

    private fun readAllPreferencesFromDataStore()
    {
        val prefs = arrayOf("location","language","wind","temperature","notification")
        viewModelScope.launch(ioDispatcher)
        {
            prefs.forEach {propertyName ->
                val property =    SettingsState::class.java.getDeclaredField(propertyName)
                property.isAccessible = true
                property.set(_state.value.copy(), readStringFromDataStoreUseCase.execute(propertyName))

            }
        }
    }


    init {
        readAllPreferencesFromDataStore()
    }

}