package com.example.weatherpilot.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.usecase.alerts.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.alerts.GetAllAlertsUseCase
import com.example.weatherpilot.domain.usecase.alerts.UpdateAlertUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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


    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow.asSharedFlow()


    fun onEvent(intent: NotificationIntent) {
        when (intent) {

            NotificationIntent.GetAllAlertsNotifications -> {
                getAllAlerts()
            }


            is NotificationIntent.DeleteAlert -> {
                deleteAlertFromDatabase(intent.item)
            }


            is NotificationIntent.UpdateAlertState -> {
                updateAlertStateToScheduled(intent.alert)
            }


        }
    }

    private fun deleteAlertFromDatabase(id: AlertItem) {
        viewModelScope.launch(ioDispatcher) {
            deleteAlertUseCase.execute(id).collect()
        }
    }

    private fun getAllAlerts() {
        viewModelScope.launch(ioDispatcher) {
            getAllAlertsUseCase.execute().collectLatest { response ->

                _alertsAndNotificationsState.update { it.copy(alertsAndNotificationsList = response.sortedBy { item -> item.time }) }

            }
        }
    }


    private fun updateAlertStateToScheduled(alert: AlertItem) {
        viewModelScope.launch(ioDispatcher) {
            updateAlertUseCase.execute(alert.copy(scheduled = true))
        }
    }


    init {
        getAllAlerts()
    }


}