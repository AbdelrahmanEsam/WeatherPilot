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

class SearchCityByNameUseCaseTest{


    private lateinit var repository: Repository
    private lateinit var searchCityByNameUseCase: SearchCityByNameUseCase


    @Before
    fun setUp() {
        repository = Mockito.mock()
        searchCityByNameUseCase = SearchCityByNameUseCase(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `call execute from use case should call getSearchResponse from repo`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            searchCityByNameUseCase.execute("any")
            Mockito.verify(repository, times(1)).getSearchResponse<String>("any")
        }
    }
}