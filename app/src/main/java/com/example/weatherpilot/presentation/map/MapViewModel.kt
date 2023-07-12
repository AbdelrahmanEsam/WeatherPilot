package com.example.weatherpilot.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.usecase.transformers.GetTimeStampUseCase
import com.example.weatherpilot.domain.usecase.alerts.InsertAlertUseCase
import com.example.weatherpilot.domain.usecase.favourites.InsertNewFavouriteUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.datastore.SaveStringToDataStoreUseCase
import com.example.weatherpilot.domain.usecase.network.SearchCityByNameUseCase
import com.example.weatherpilot.domain.usecase.alerts.UpdateAlertUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import com.example.weatherpilot.util.usescases.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class MapViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase,
    private val insertNewFavouriteToDatabase: InsertNewFavouriteUseCase,
    private val readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase,
    private val insertAlertUseCase: InsertAlertUseCase,
    private val getTimeStampUseCase: GetTimeStampUseCase,
    private val updateAlertUseCase: UpdateAlertUseCase,
    private val searchCityByNameUseCase: SearchCityByNameUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<MapState.RegularMapState> =
        MutableStateFlow(MapState.RegularMapState())
    val state = _state.asStateFlow()


    private val _favouriteState: MutableStateFlow<MapState.FavouriteMapState> =
        MutableStateFlow(MapState.FavouriteMapState())
    val favouriteState = _favouriteState.asStateFlow()


    private val _alertState: MutableStateFlow<MapState.AlertMapState> =
        MutableStateFlow(MapState.AlertMapState())
    val alertState = _alertState.asStateFlow()


    private val _searchResultState: MutableStateFlow<MapState.SearchResultState> =
        MutableStateFlow(MapState.SearchResultState())
    val searchResultState = _searchResultState.asStateFlow()


    private val _snackBarFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<Int> = _snackBarFlow.asSharedFlow()


    fun onEvent(intent: MapIntent) {
        when (intent) {
            MapIntent.SaveLocationToDataStore -> saveLatLongToDataStore()
            is MapIntent.NewLatLong -> with(intent) {
                _state.update {
                    it.copy(
                        longitude = longitude,
                        latitude = latitude
                    )
                }
            }

            is MapIntent.MapLoaded -> {
                when (intent.stateType) {
                    "from regular fragment" -> {
                        _state.update {
                            it.copy(mapLoadingState = false)

                        }
                    }

                    "from favourite Fragment" -> {
                        _favouriteState.update { it.copy(mapLoadingState = false) }
                    }

                    else -> {
                        _alertState.update { it.copy(mapLoadingState = false) }
                    }
                }


            }


            is MapIntent.NewFavouriteLocation -> {
                _favouriteState.update {
                    it.copy(
                        arabicName = intent.arabicName,
                        englishName = intent.englishName,
                        latitude = intent.latitude,
                        longitude = intent.longitude
                    )
                }
            }

            is MapIntent.SaveFavourite -> {
                saveLocationToDatabase()
            }


            is MapIntent.AlertLocationIntent -> {
                _alertState.update {
                    it.copy(
                        arabicName = intent.arabicName,
                        englishName = intent.englishName,
                        latitude = intent.latitude,
                        longitude = intent.longitude
                    )
                }
            }

            MapIntent.SaveAlert -> {
                saveAlertToDatabase()
            }

            is MapIntent.ShowSnackBar -> viewModelScope.launch { _snackBarFlow.emit(intent.message) }
            is MapIntent.SetAlarmDateIntent -> _alertState.update { it.copy(date = intent.date) }
            is MapIntent.SetAlarmTimeIntent -> _alertState.update { it.copy(time = intent.time) }

            is MapIntent.UpdateAlertStateToScheduled -> {
                updateAlertStateToScheduled(intent.alert)
            }

            is MapIntent.SearchCityName -> {
                searchCityByName(intent.cityName)
            }

            MapIntent.ClearSearchList -> {
                _searchResultState.update { it.copy(searchResult = null) }
            }

            is MapIntent.SetAlarmType -> _alertState.update { it.copy(kind = intent.type) }
        }

    }


    private fun readLocationLatLonFromDataStore() {

        viewModelScope.launch(ioDispatcher) {
            readStringFromDataStoreUseCase.execute<String?>("latitude")
                .combine(readStringFromDataStoreUseCase.execute<String?>("longitude")) { latResponse, longResponse ->

                    if (latResponse is Response.Success && longResponse is Response.Success) {

                        _state.update {
                            it.copy(
                                longitude = longResponse.data,
                                latitude = latResponse.data
                            )
                        }
                    }
                }.collect()
        }
    }

    private fun saveLatLongToDataStore() {
        viewModelScope.launch(ioDispatcher)
        {
            if (!_state.value.latitude.isNullOrBlank()) {

                _state.update { it.copy(saveState = false) }
                with(_state.value) {
                    saveStringToDataStoreUseCase.execute("latitude", latitude!!)
                    saveStringToDataStoreUseCase.execute("longitude", longitude!!)
                }
            }
            _state.update { it.copy(saveState = true) }
        }
    }

    private fun saveLocationToDatabase() {
        viewModelScope.launch(ioDispatcher) {
            if (_favouriteState.value.arabicName.isNotEmpty()) {
                insertNewFavouriteToDatabase.execute(
                    Location(
                        arabicName = _favouriteState.value.arabicName,
                        englishName = _favouriteState.value.englishName,
                        longitude = _favouriteState.value.longitude,
                        latitude = _favouriteState.value.latitude
                    )
                ).collectLatest { insertResponse ->

                    when (insertResponse) {
                        is Response.Success -> {
                            _favouriteState.update {
                                it.copy(
                                    insertFavouriteResult = true,
                                    saveState = true
                                )
                            }
                            _snackBarFlow.emit(R.string.successful_insert)
                        }

                        else -> _snackBarFlow.emit(R.string.unknown_error)
                    }

                }
            } else {
                _snackBarFlow.emit(R.string.unknown_error)
            }
        }
    }


    private fun saveAlertToDatabase() {
        viewModelScope.launch(ioDispatcher) {
            with(_alertState.value) {

                val timeStamp = getTimeStampUseCase.execute("$date $time")
                timeStamp?.let {


                    insertAlertUseCase.execute(
                        AlertItem(
                            alarmId = Random.nextInt(),
                            arabicName = arabicName,
                            englishName = englishName,
                            longitude = longitude,
                            latitude = latitude,
                            time = timeStamp,
                            kind = kind,
                        )
                    ).collectLatest { response ->
                        when (response) {
                            is Response.Success -> _snackBarFlow.emit(R.string.successful_insert)
                            else -> _snackBarFlow.emit(R.string.unknown_error)
                        }

                    }

                    _alertState.update { it.copy(saveState = true) }
                } ?: run {
                    _snackBarFlow.emit(R.string.unknown_error)
                }
            }
        }

    }




    private fun updateAlertStateToScheduled(alert: AlertItem) {
        viewModelScope.launch(ioDispatcher) {
            updateAlertUseCase.execute(alert.copy(scheduled = true)).collect()
        }
    }


    private fun searchCityByName(cityName: String) {
        viewModelScope.launch(ioDispatcher) {
            searchCityByNameUseCase.execute(cityName).collect { searchResponse ->

                when (searchResponse) {
                    is Response.Failure -> {
                        _searchResultState.update { it.copy(loading = false) }
                    }

                    is Response.Loading -> {
                        _searchResultState.update { it.copy(loading = true) }
                    }

                    is Response.Success -> {
                        _searchResultState.update {
                            it.copy(
                                searchResult = searchResponse.data,
                                loading = false
                            )
                        }
                    }
                }
            }
        }
    }

    init {
        readLocationLatLonFromDataStore()
    }


}