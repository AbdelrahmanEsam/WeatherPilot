package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class DeleteFavouriteFavouriteUseCaseTest {


    private lateinit var deleteFavouriteFavouriteUseCase: DeleteFavouriteFavouriteUseCase
    private lateinit var fakeRepository: Repository

    @Before
    fun setUp()  = runTest{
         fakeRepository = FakeRepository()
        deleteFavouriteFavouriteUseCase = DeleteFavouriteFavouriteUseCase(fakeRepository)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete favourite location from database`()  = runTest(UnconfinedTestDispatcher()) {
        val location = FavouriteLocation(1,"القاهرة", "cairo", latitude = "1.0", longitude = "2.0")
        val response : Flow<Response<String>> =   fakeRepository.insertFavouriteLocation(location)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { response.collect()}
        launch {  fakeRepository.deleteFavouriteLocation(latitude = "1.0", longitude = "2.0")}
        val resultList = mutableListOf<List<Location>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeRepository.getFavourites().toList(resultList)
        }
        assertEquals(0,fakeRepository.getFavourites().first().size)
    }
}