package com.example.weatherpilot.domain.usecase.cached

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.times


@OptIn(ExperimentalCoroutinesApi::class)
internal class UpdateCityNameUseCaseTest
{

    @Mock
    private lateinit  var fakeRepository: Repository
    private lateinit  var updateCityNameUseCase: UpdateCityNameUseCase


    @Before
    fun setUp()
    {
        fakeRepository = Mockito.mock()
        updateCityNameUseCase = UpdateCityNameUseCase(fakeRepository)
    }

    @Test
    fun `execute should call getCachedResponse   from repository`() = runTest(
        UnconfinedTestDispatcher()
    )
    {


        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            updateCityNameUseCase.execute<String>("city")
            Mockito.verify(fakeRepository, times(1)).updateResponseToDatabase<Response<String>>("city")
        }
    }
}