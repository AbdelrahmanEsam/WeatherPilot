package com.example.weatherpilot.presentation.firsttimedialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.usecase.SaveStringToDataStoreUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstTimeViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<FirstTimeState> = MutableStateFlow(FirstTimeState())
    val state = _state.asStateFlow()

    private val _snackBarState: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarState = _snackBarState.asSharedFlow()


    fun onEvent(intent: FirstTimeIntent) {
        when (intent) {
            is FirstTimeIntent.NotificationStateChanged -> _state.update { it.copy(notificationType = intent.newNotificationState) }
            is FirstTimeIntent.PlaceStateChanged -> _state.update { it.copy(locationType = intent.gps) }
            FirstTimeIntent.Go -> savePrefsToDataStore()
        }
    }


    private fun savePrefsToDataStore() {

        viewModelScope.launch(ioDispatcher) {
            if (_state.value.locationType.isEmpty()) {
                _snackBarState.emit("please choose location type")
                return@launch
            }

            Log.d("location",_state.value.locationType +" "+_state.value.notificationType)
            FirstTimeState::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                val value = field.get(_state.value) as String
                Log.d(field.name,value)
                saveStringToDataStoreUseCase.execute(key = field.name, value)
            }
        }
    }
}