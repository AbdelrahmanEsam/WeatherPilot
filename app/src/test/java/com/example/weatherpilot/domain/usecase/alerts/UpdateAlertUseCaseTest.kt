package com.example.weatherpilot.domain.usecase.alerts

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.local.FakeLocalDataSourceImpl
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test

class UpdateAlertUseCaseTest{


    private lateinit var repository: Repository
    private lateinit var updateAlertUseCase: UpdateAlertUseCase

    private val alerts: MutableList<SavedAlert> = mutableListOf(
        SavedAlert(
            id = 0,
            arabicName = "القاهرة",
            englishName = "cairo", longitude = "10.66",
            latitude = "15.66",
            time = 1234567898544,
            message = "weather is fine",
            kind = "notification",
            scheduled = true
        ),

        SavedAlert(
            id = 0,
            arabicName = "الاسكندرية",
            englishName = "alexandria", longitude = "20.66",
            latitude = "20.66",
            time = 1234567898547,
            message = "weather is fine",
            kind = "notification",
            scheduled = false
        ),


        SavedAlert(
            id = 0,
            arabicName = "الرياض",
            englishName = "riyadh", longitude = "30.66",
            latitude = "30.66",
            time = 1234567898546,
            message = "weather is fine",
            kind = "alert",
            scheduled = true
        ),


        SavedAlert(
            id = 0,
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
    fun setUp()
    {
        repository = FakeRepository(alerts = alerts)
        updateAlertUseCase = UpdateAlertUseCase(repository)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `update alert from the localDataSource should return success message`() = runTest{
            val alert = alerts.first()
            MatcherAssert.assertThat(alert.arabicName , CoreMatchers.equalTo("القاهرة"))
            val alertsFlow = updateAlertUseCase.execute(alert.copy(arabicName = "المنصورة").toAlertItem())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    MatcherAssert.assertThat((it as Response.Success<String>).data, CoreMatchers.equalTo("success"))
                    MatcherAssert.assertThat(alerts.first {  it.time == alert.time }.arabicName , CoreMatchers.equalTo("المنصورة"))
                }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `update alert from the localDataSource with general exception should return success message`() = runTest{
            val alert = alerts.first()
            MatcherAssert.assertThat(alert.arabicName , CoreMatchers.equalTo("القاهرة"))
            (repository as FakeRepository).setShouldReturnGeneralError(true)
            val alertsFlow = updateAlertUseCase.execute(alert.copy(arabicName = "المنصورة").toAlertItem())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    MatcherAssert.assertThat((it as Response.Failure<String>).error, CoreMatchers.equalTo("error"))
                    MatcherAssert.assertThat(alerts.first {  it.time == alert.time }.arabicName , CoreMatchers.equalTo("القاهرة"))
                }
            }
        }


}