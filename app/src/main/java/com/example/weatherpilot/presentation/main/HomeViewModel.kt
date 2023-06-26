package com.example.weatherpilot.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.usecase.GetCurrentTimeStampUseCase
import com.example.weatherpilot.domain.usecase.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.presentation.settings.SettingsState
import com.example.weatherpilot.util.Dispatcher
import com.example.weatherpilot.util.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.roundToInt


@HiltViewModel
class HomeViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getCurrentTimeStampUseCase: GetCurrentTimeStampUseCase,
    private val readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase
) : ViewModel() {


    private val _stateDisplay : MutableStateFlow<HomeState.Display> = MutableStateFlow(HomeState.Display())
     val stateDisplay = _stateDisplay.asStateFlow()


    private val _stateLongLat : MutableStateFlow<HomeState.LongLat> = MutableStateFlow(HomeState.LongLat())
    val stateLongLat = _stateLongLat.asStateFlow()


    private val _statePreferences : MutableStateFlow<HomeState.Preferences> = MutableStateFlow(HomeState.Preferences())
    val statePreferences = _statePreferences.asStateFlow()


    fun onEvent(intent: HomeIntent)
    {
        when(intent){
            is HomeIntent.NewLocationFromGPS -> {
                    _stateLongLat.update { it.copy(longitude = intent.longitude, latitude = intent.latitude) }
                    getWeatherResponse()
            }
            HomeIntent.ReadLatLongFromDataStore -> readLocationLatLonFromDataStore()
        }

    }

    private fun getWeatherResponse()
    {

        viewModelScope.launch(ioDispatcher) {


             val weatherResponse =
             getWeatherDataUseCase.execute(longitude = stateLongLat.value.longitude!!
                 , latitude =  stateLongLat.value.latitude!!
                 )
              with(weatherResponse){ _stateDisplay.update {
                  it.copy(city = city, weatherState =   description
                    , pressure =  pressure.toString()
                    , clouds = clouds.toString()
                    , humidity = humidity.toString()
                    , wind = wind.toString()
                    , dayState = hoursWeather
                    , temp = temp.roundToInt().toString()
                    , visibility = visibility.toString()
                    , iconCode = icon
                    , weekState = daysWeather ?: listOf()
                )
                }}




        }
    }

    private fun readLocationLatLonFromDataStore()
    {
        viewModelScope.launch(ioDispatcher) {
          readStringFromDataStoreUseCase.execute("latitude")
              .combine(readStringFromDataStoreUseCase.execute("longitude")){ lat , long ->
                  _stateLongLat.update { it.copy(latitude = lat, longitude = long) }
              }.distinctUntilChanged().collect{
                  Log.d("read longlat",_stateLongLat.value.toString())
                  getWeatherResponse()
              }

        }
    }


    private fun readAllPreferencesFromDataStore()
    {


        HomeState.Preferences::class.java.declaredFields.forEach { field ->
        viewModelScope.launch(ioDispatcher)
        {


                    val property =   HomeState.Preferences::class.java.getDeclaredField(field.name)
                    property.isAccessible = true
                    readStringFromDataStoreUseCase.execute(field.name).collect{
                    val newState = _statePreferences.value.copy()
                    property.set(newState,it)
                    _statePreferences.update { newState }
                }

            }
        }
    }



    init {
        readAllPreferencesFromDataStore()
    }


}