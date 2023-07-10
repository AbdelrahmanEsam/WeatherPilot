package com.example.weatherpilot.presentation.notification

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.alerts.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.alerts.GetAllAlertsUseCase
import com.example.weatherpilot.domain.usecase.alerts.UpdateAlertUseCase
import com.example.weatherpilot.util.DispatcherTestingRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationsViewModelTest {

    @get:Rule
    val mainDispatcherTestingRule = DispatcherTestingRule()

    private lateinit var repository: Repository
    private lateinit var getAllAlertsUseCase: GetAllAlertsUseCase
    private lateinit var deleteAllAlertsUseCase: DeleteAlertUseCase
    private lateinit var updateAlertUseCase: UpdateAlertUseCase
    private lateinit var viewModel: NotificationsViewModel

    private val alerts: MutableList<SavedAlert> = mutableListOf(
        SavedAlert(
            id = 1,
            arabicName = "القاهرة",
            englishName = "cairo", longitude = "10.66",
            latitude = "15.66",
            time = 1234567898544,
            message = "weather is fine",
            kind = "notification",
            scheduled = false
        ),

        SavedAlert(
            id = 2,
            arabicName = "الاسكندرية",
            englishName = "alexandria", longitude = "20.66",
            latitude = "20.66",
            time = 1234567898547,
            message = "weather is fine",
            kind = "notification",
            scheduled = false
        ),


        SavedAlert(
            id = 3,
            arabicName = "الرياض",
            englishName = "riyadh", longitude = "30.66",
            latitude = "30.66",
            time = 1234567898546,
            message = "weather is fine",
            kind = "alert",
            scheduled = true
        ),


        SavedAlert(
            id = 4,
            arabicName = "الدوحة",
            englishName = "doha", longitude = "40.66",
            latitude = "40.66",
            time = 1234567898854,
            message = "weather is bad",
            kind = "alert",
            scheduled = false
        ),
    )

    @Before
    fun setUp() {
        repository = FakeRepository(alerts = alerts)
        getAllAlertsUseCase = GetAllAlertsUseCase(repository)
        deleteAllAlertsUseCase = DeleteAlertUseCase(repository)
        updateAlertUseCase = UpdateAlertUseCase(repository)
        viewModel = NotificationsViewModel(
            ioDispatcher = kotlinx.coroutines.Dispatchers.Main,
            getAllAlertsUseCase,
            deleteAllAlertsUseCase,
            updateAlertUseCase
        )
    }


    @Test
    fun `init viewModel should get All alerts and list them`() = runTest {
        viewModel.onEvent(NotificationIntent.GetAllAlertsNotifications)
        backgroundScope.launch {
            viewModel.alertsAndNotificationsState.collect()
        }
        MatcherAssert.assertThat(
            viewModel.alertsAndNotificationsState.value.alertsAndNotificationsList.size,
            CoreMatchers.equalTo(alerts.size)
        )
    }

    @Test
    fun `send UpdateAlertState should update the alert to scheduled with the sent id and get all alerts from the data source and update the state`() =
        runTest {

            backgroundScope.launch{
                viewModel.alertsAndNotificationsState.collect()
            }
            MatcherAssert.assertThat(
                viewModel.alertsAndNotificationsState.value.alertsAndNotificationsList.first().scheduled,
                CoreMatchers.equalTo(false)
            )

           val updateAlert =  alerts.first().toAlertItem()
            viewModel.onEvent(
                NotificationIntent.UpdateAlertState(
                    updateAlert
                )
            )

            viewModel.onEvent(NotificationIntent.GetAllAlertsNotifications)

            MatcherAssert.assertThat(
                viewModel.alertsAndNotificationsState.value.alertsAndNotificationsList.find { it.time == updateAlert.time}?.scheduled,
                CoreMatchers.equalTo(true)
            )


        }

    @Test
    fun `delete alert from database should update the list and the size should less`() = runTest {
        backgroundScope.launch{
            viewModel.alertsAndNotificationsState.collect()
        }
        val updateAlert =  alerts.first().toAlertItem()
        MatcherAssert.assertThat(
            viewModel.alertsAndNotificationsState.value.alertsAndNotificationsList.find { it.time == updateAlert.time}?.time,
            CoreMatchers.equalTo(updateAlert.time)
        )


        viewModel.onEvent(
            NotificationIntent.DeleteAlert(
                updateAlert
            )
        )

        viewModel.onEvent(NotificationIntent.GetAllAlertsNotifications)

        MatcherAssert.assertThat(
            viewModel.alertsAndNotificationsState.value.alertsAndNotificationsList.find { it.time == updateAlert.time},
            CoreMatchers.equalTo(null)
        )

    }
}