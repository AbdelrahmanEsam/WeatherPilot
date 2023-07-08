package com.example.weatherpilot.domain.usecase.alerts

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.times


@OptIn(ExperimentalCoroutinesApi::class)
class DeleteAlertUseCaseTest {
//    private lateinit var deleteAlertUseCase: DeleteAlertUseCase
//    private lateinit var fakeRepository: Repository


    private var fakeRepository: Repository = Mockito.mock()
    private var deleteAlertUseCase = DeleteAlertUseCase(fakeRepository)


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


//    @Before
//    fun setUp()  = runTest{
//
//        fakeRepository = FakeRepository(alerts =  alerts)
//        deleteAlertUseCase = DeleteAlertUseCase(fakeRepository)
//    }
//
//
//
//
//    @Test
//    fun `delete alert item from database should return flow with response success and item is deleted from my list`() = runTest {
//
//        val alert =  alerts.first()
//        MatcherAssert.assertThat(alerts.contains(alert), CoreMatchers.equalTo(true))
//        val alertsFlow = deleteAlertUseCase.execute(alerts.first().toAlertItem())
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//            alertsFlow.collectLatest {
//                MatcherAssert.assertThat(
//                    (it as Response.Success<String>).data,
//                    CoreMatchers.equalTo("success")
//                )
//                MatcherAssert.assertThat(alerts.size, CoreMatchers.equalTo(3))
//                MatcherAssert.assertThat(alerts.contains(alert), CoreMatchers.equalTo(false))
//            }
//        }
//    }


    @Test
    fun `execute should call delete alert from repository`() = runTest(UnconfinedTestDispatcher())
        {
            val item = alerts.first()

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                deleteAlertUseCase.execute(item.toAlertItem())
                Mockito.verify(fakeRepository, times(1)).deleteAlertFromDatabase<AlertItem>(item)
            }
        }


}