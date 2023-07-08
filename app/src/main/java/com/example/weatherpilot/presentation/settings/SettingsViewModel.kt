package com.example.weatherpilot.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.datastore.SaveStringToDataStoreUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.weatherpilot.util.usescases.Response.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase,
    private val readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase
) : ViewModel() {

    private val _state : MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()


    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow.asSharedFlow()


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

    private fun saveStringToDataStore(key : String, value : String) {
        viewModelScope.launch(ioDispatcher)
        {
            saveStringToDataStoreUseCase.execute(key, value).collect { response ->


                when (response) {
                    is Success -> {
                        _snackBarFlow.emit(response.data!!)
                    }

                    else -> {
                        _snackBarFlow.emit(response.error!!)
                    }
                }
            }
        }
    }

    private fun readAllPreferencesFromDataStore()
    {


        SettingsState::class.java.declaredFields.forEach {field ->
        viewModelScope.launch(ioDispatcher)
        {

                val property =  SettingsState::class.java.getDeclaredField(field.name)
                    property.isAccessible = true

                readStringFromDataStoreUseCase.execute<String?>(property.name).collect{ response ->

                            Log.d("newState",response.data.toString())
                    when (response){
                        is Success -> {
                            val newState = _state.value.copy()
                            property.set(newState,response.data)
                            _state.update { newState }
                        }
                        else ->{
                            Log.d("newState", "error")
                            _snackBarFlow.emit(response.error!!)
                        }
                    }


                }
        }
        }
    }

    init {
        readAllPreferencesFromDataStore()
    }

}