package com.example.weatherpilot.domain.usecase.network

import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.times


@OptIn(ExperimentalCoroutinesApi::class)
class GetWeatherDataUseCaseTest
{
    private lateinit var repository: Repository
    private lateinit var getWeatherDataUseCase: GetWeatherDataUseCase


    @Before
    fun setUp() {
       repository = Mockito.mock()
        getWeatherDataUseCase = GetWeatherDataUseCase(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `call execute from use case should call getWeatherResponse from repo`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getWeatherDataUseCase.execute("any" , "any","en")
            Mockito.verify(repository, times(1)).getWeatherResponse<String>("any","any","en")
        }
    }




}