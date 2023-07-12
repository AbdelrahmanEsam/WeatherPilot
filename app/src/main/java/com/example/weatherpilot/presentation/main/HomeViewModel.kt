package com.example.weatherpilot.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.model.SearchResponse
import com.example.weatherpilot.domain.usecase.cached.GetCachedResponseUseCase
import com.example.weatherpilot.domain.usecase.cached.UpdateCityNameUseCase
import com.example.weatherpilot.domain.usecase.transformers.GetCurrentDateUseCase
import com.example.weatherpilot.domain.usecase.network.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.network.SearchCityByNameUseCase
import com.example.weatherpilot.domain.usecase.transformers.TempTransformerUseCase
import com.example.weatherpilot.domain.usecase.transformers.Temperature
import com.example.weatherpilot.domain.usecase.transformers.WindSpeedTransformerUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import com.example.weatherpilot.util.usescases.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt


@HiltViewModel
class HomeViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase,
    private val windSpeedTransformerUseCase: WindSpeedTransformerUseCase,
    private val tempTransformerUseCase: TempTransformerUseCase,
    private val searchCityByNameUseCase: SearchCityByNameUseCase,
    private val getCachedResponseUseCase: GetCachedResponseUseCase,
    private val updateCityNameUseCase: UpdateCityNameUseCase
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

            is HomeIntent.FetchDataOfFavouriteLocation -> {
                _stateLongLat.update { it.copy(longitude = intent.longitude, latitude = intent.latitude) }
                _statePreferences.update { it.copy(locationType = "favourite") }
                getWeatherResponse()
            }

            HomeIntent.ReadLatLongFromDataStore -> readLocationLatLonFromDataStore()
            HomeIntent.FetchData -> getWeatherResponse()
            HomeIntent.ReadPrefsFromDataStore -> readAllPreferencesFromDataStore()
        }

    }

    private fun getWeatherResponse()
    {

        viewModelScope.launch(ioDispatcher) {
            stateLongLat.value.longitude?.let {
                _stateDisplay.update { it.copy(loading = true) }
                val weatherResponse =
                    getWeatherDataUseCase.execute(longitude = stateLongLat.value.longitude!!
                        , latitude =  stateLongLat.value.latitude!!
                        ,statePreferences.value.languageType ?: "en"
                    )

                weatherResponse.collectLatest { response ->
                    when(response){
                        is Response.Failure -> {
                            _stateDisplay.update { it.copy(error = response.error, loading = false) }
                            getCachedResponse()
                        }
                        is Response.Loading ->{
                            _stateDisplay.update { it.copy(loading = true) }
                        }
                        is Response.Success -> {
                            getCachedResponse()
                        }
                    }
                }

            } ?: kotlin.run {
                _stateDisplay.update { it.copy(loading = false, error = "invalid location") }
            }
        }
    }

    private suspend fun getLocalCityName(city : String,local : String)
    {
        searchCityByNameUseCase.execute(city).collectLatest { response ->
            when(response){
                is Response.Success -> {
                    if (local == "ar"){
                        val localCityName :String =   (response.data as SearchResponse).searchResults.first()?.LocalNames?.ar  ?: response.data.searchResults.first()?.LocalNames?.en!!
                      _stateDisplay.update { it.copy(city = localCityName)}
                        updateCityNameUseCase.execute<Response<String>>(_stateDisplay.value.city!!)
                    }else{
                      _stateDisplay.update { it.copy(city = (response.data as SearchResponse).searchResults.first()?.LocalNames?.en) }
                    }
                }
                else -> {_stateDisplay.update { it.copy(city = city) }}
            }
        }
    }

    private fun readLocationLatLonFromDataStore()
    {
        viewModelScope.launch(ioDispatcher) {
          readStringFromDataStoreUseCase.execute<String?>("latitude")
              .combine(readStringFromDataStoreUseCase.execute<String?>("longitude")){ latResponse , longResponse ->
                  if (latResponse is Response.Success &&  longResponse is Response.Success){
                      _stateLongLat.update { it.copy(latitude = latResponse.data, longitude = longResponse.data) }
                  }
              }.distinctUntilChanged().collect{
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
                    readStringFromDataStoreUseCase.execute<String?>(field.name)
                        .collect{

                    val newState = _statePreferences.value.copy()
                    property.set(newState,it.data)
                    _statePreferences.update { newState }

                }

            }
        }
    }


    private fun getCachedResponse() {
        viewModelScope.launch(ioDispatcher) {
            _stateDisplay.update { it.copy(loading = true) }
            val weatherResponse =
                getCachedResponseUseCase.execute()

            weatherResponse.collectLatest { response ->

                if (response.isEmpty()) return@collectLatest
                with(response.first()){
                            _stateDisplay.update {
                                val tempPref =
                                    when (_statePreferences.value.temperatureType.toString()) {
                                        "F" -> Temperature.Fahrenheit(temp.roundToInt())
                                        "K" -> Temperature.Kelvin(temp.roundToInt())
                                        else -> Temperature.Celsius(temp.roundToInt())
                                    }

                                getLocalCityName(city, statePreferences.value.languageType ?: "en")

                                it.copy(
                                    weatherState = description,
                                    pressure = pressure.toString(),
                                    clouds = clouds.toString(),
                                    humidity = humidity.toString(),
                                    wind = windSpeedTransformerUseCase.execute(
                                        wind,
                                        _statePreferences.value.windType.toString()
                                    ),
                                    dayState = hoursWeather,
                                    temp = tempTransformerUseCase.execute(tempPref),
                                    visibility = visibility.toString(),
                                    iconCode = icon,
                                    weekState = daysWeather ?: listOf(),
                                    date = getCurrentDateUseCase.execute(),
                                    loading = false
                                )
                            }
            }
            }
        }
    }




}