package com.example.weatherpilot.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.usecase.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.GetAllAlertsUseCase
import com.example.weatherpilot.domain.usecase.UpdateAlertUseCase
import com.example.weatherpilot.util.coroutines.Dispatcher
import com.example.weatherpilot.util.coroutines.Dispatchers
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
    private val deleteAlertUseCase: DeleteAlertUseCase,
    private val updateAlertUseCase: UpdateAlertUseCase
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


            is  NotificationIntent.UpdateAlertState ->{
                updateAlertStateToScheduled(intent.alert)
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


    private fun updateAlertStateToScheduled(alert : AlertItem)
    {
        viewModelScope.launch(ioDispatcher) {
            updateAlertUseCase.execute(alert.copy(scheduled =  true))
        }
    }


    init {
        getAllAlerts()
    }


}