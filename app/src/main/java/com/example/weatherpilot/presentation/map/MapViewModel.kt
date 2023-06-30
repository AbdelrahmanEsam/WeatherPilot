package com.example.weatherpilot.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.usecase.InsertNewFavouriteUseCase
import com.example.weatherpilot.domain.usecase.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.SaveStringToDataStoreUseCase
import com.example.weatherpilot.util.Dispatcher
import com.example.weatherpilot.util.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase,
    private val insertNewFavouriteToDatabase: InsertNewFavouriteUseCase,
    private val readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<MapState.RegularMapState> =
        MutableStateFlow(MapState.RegularMapState())
    val state = _state.asStateFlow()


    private val _favouriteState: MutableStateFlow<MapState.FavouriteMapState> =
        MutableStateFlow(MapState.FavouriteMapState())
    val favouriteState = _favouriteState.asStateFlow()





    fun onEvent(intent: MapIntent) {
        when (intent) {
            MapIntent.SaveDataToDataStore -> saveLatLongToDataStore()
            is MapIntent.NewLatLong -> with(intent) {
                _state.update {
                    it.copy(
                        longitude = longitude,
                        latitude = latitude
                    )
                }
            }

            MapIntent.MapLoaded -> _state.update { it.copy(mapLoadingState = false) }


            is MapIntent.SaveFavourite -> {
                saveLocationToDatabase()
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
        }

    }


    private fun readLocationLatLonFromDataStore()
    {

        viewModelScope.launch(ioDispatcher) {
            readStringFromDataStoreUseCase.execute("latitude")
                .combine(readStringFromDataStoreUseCase.execute("longitude")){ lat , long ->
                    _state.update { it.copy(longitude = long.toString(), latitude = lat.toString()) }
                }.collect()
        }
    }

    private fun saveLatLongToDataStore() {
        viewModelScope.launch(ioDispatcher)
        {
            if (_state.value.latitude.isNotEmpty()) {
                _state.update { it.copy(saveState = false) }
                with(_state.value) { saveStringToDataStoreUseCase.execute("latitude", latitude) }
                with(_state.value) { saveStringToDataStoreUseCase.execute("longitude", longitude) }
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
                )
            }
        }
    }


    init {
        readLocationLatLonFromDataStore()
    }


}