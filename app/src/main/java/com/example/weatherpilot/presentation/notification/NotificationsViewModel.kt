package com.example.weatherpilot.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.usecase.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.GetAllAlertsUseCase
import com.example.weatherpilot.util.Dispatcher
import com.example.weatherpilot.util.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getAllAlertsUseCase: GetAllAlertsUseCase,
    private val deleteAlertUseCase: DeleteAlertUseCase
) : ViewModel() {


    private val _alertsAndNotificationsState: MutableStateFlow<AlertsAndNotificationsState> =
        MutableStateFlow(
            AlertsAndNotificationsState()
        )
    val alertsAndNotificationsState = _alertsAndNotificationsState.asStateFlow()


    fun onEvent(intent: NotificationIntent) {
        when (intent) {

            NotificationIntent.GetAllAlertsNotifications -> {
                getAllAlerts()
            }


            is NotificationIntent.DeleteAlert ->{
                deleteAlertFromDatabase(intent.item)
            }


        }
    }

    private fun deleteAlertFromDatabase(id: AlertItem) {
        viewModelScope.launch(ioDispatcher){
             deleteAlertUseCase.execute(id)
        }
    }

    private fun getAllAlerts() {
        viewModelScope.launch(ioDispatcher) {
            getAllAlertsUseCase.execute().collectLatest { alertsList ->
                _alertsAndNotificationsState.update { it.copy(alertsAndNotificationsList = alertsList) }
            }
        }
    }


    init {
        getAllAlerts()
    }


}