package com.example.weatherpilot.domain.usecase.alerts

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.alerts.GetAllAlertsUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.times

class GetAllAlertsUseCaseTest{


//    private lateinit var  fakeRepository: Repository
    private lateinit var getAllAlertsUseCase: GetAllAlertsUseCase

    @Mock
    private lateinit var  fakeRepository: Repository

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
//        fakeRepository = FakeRepository(alerts = alerts)

        fakeRepository = Mockito.mock()
        getAllAlertsUseCase = GetAllAlertsUseCase(fakeRepository)
    }


//    @Test
//    fun `get all alerts from the local data source should return the same 4 items of the fake list`() = runTest{
//            val alertsFlow = fakeRepository.getAlerts<List<SavedAlert>>()
//            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//                alertsFlow.collectLatest {
//                    MatcherAssert.assertThat(alerts == it.data, CoreMatchers.equalTo(true))
//                    MatcherAssert.assertThat(alerts.size, CoreMatchers.equalTo(4))
//                }
//            }
//        }
//
//
//    @Test
//    fun `get all alerts from the local data source with simulated exception should return flow with error message`() = runTest {
//            (fakeRepository as FakeRepository).setShouldReturnGeneralError(true)
//            val alertsFlow = fakeRepository.getAlerts<List<SavedAlert>>()
//            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//                alertsFlow.collectLatest {
//                    MatcherAssert.assertThat(it.error, CoreMatchers.equalTo("error"))
//                    MatcherAssert.assertThat(it.data, CoreMatchers.equalTo(null))
//
//                }
//            }
//        }


    @Test
    fun `execute fun in use case should call the get All Alerts fun from the repository`()  = runTest(UnconfinedTestDispatcher())
    {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAllAlertsUseCase.execute()
            Mockito.verify(fakeRepository, times(1)).getAlerts()
        }

    }



}