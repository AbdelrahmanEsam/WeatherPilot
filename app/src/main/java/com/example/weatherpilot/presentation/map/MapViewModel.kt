package com.example.weatherpilot.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MapViewModel @Inject constructor(
    private val saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase,
     @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher
     ) : ViewModel() {

    private val _state : MutableStateFlow<MapState> = MutableStateFlow(MapState())
    val state = _state.asStateFlow()


         fun onEvent(intent: MapIntent)
         {
             when(intent)
             {
                 MapIntent.SaveDataToDataStore -> saveLatLongToDataStore()
                 is MapIntent.NewLatLong -> with(intent){ _state.update { it.copy(
                     longitude = longitude,
                     latitude = latitude
                 ) }}

                 MapIntent.MapLoaded -> _state.update { it.copy(mapLoadingState = false) }
             }

         }

    private fun saveLatLongToDataStore()
    {
        viewModelScope.launch(ioDispatcher)
        {
            if (_state.value.latitude.isNotEmpty()){
                _state.update { it.copy(saveState = false) }
                with(_state.value){saveStringToDataStoreUseCase.execute(longitude,latitude)}
            }
            _state.update { it.copy(saveState = true) }
        }
    }


}