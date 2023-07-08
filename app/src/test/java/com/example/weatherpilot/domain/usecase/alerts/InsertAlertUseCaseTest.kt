package com.example.weatherpilot.domain.usecase.alerts

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.times

class InsertAlertUseCaseTest {

    private lateinit var insertAlertUseCase: InsertAlertUseCase
    //private lateinit var fakeRepository: Repository

    private  var fakeRepository : Repository = Mockito.mock()

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
    fun setUp() = runTest {

       // fakeRepository = FakeRepository(alerts = alerts)
        insertAlertUseCase = InsertAlertUseCase(fakeRepository)
    }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `insert alert to database should return flow with success message`() = runTest{
//
//         val alert = alerts.first().copy(id = 5)
//        assertThat(alerts.size,equalTo(4))
//        assertThat(alerts.contains(alert),equalTo(false))
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//
//            insertAlertUseCase.execute(alert.toAlertItem()).collect{
//                assertThat(it.data,equalTo("success"))
//                assertThat(alerts.size,equalTo(5))
//                assertThat(alerts.contains(alert),equalTo(true))
//            }
//        }
//    }
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `insert alert to database with simulated exception should return flow with success message`() = runTest{
//
//        val alert = alerts.first().copy(id = 5)
//        assertThat(alerts.size,equalTo(4))
//        assertThat(alerts.contains(alert),equalTo(false))
//        (fakeRepository as FakeRepository).setShouldReturnGeneralError(true)
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//
//            insertAlertUseCase.execute(alert.toAlertItem()).collect{
//                assertThat(it.error,equalTo("error"))
//                assertThat(alerts.size,equalTo(4))
//                assertThat(alerts.contains(alert),equalTo(false))
//            }
//        }
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `call execute function from the use case should call the insert alert function in repo`() = runTest{
        val alert = alerts.first()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            insertAlertUseCase.execute(alert.toAlertItem())
            Mockito.verify(fakeRepository, times(1)).insertAlertToDatabase<AlertItem>(alert)
        }
        }


}