package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class DeleteFavouriteFavouriteUseCaseTest {


    private lateinit var deleteFavouriteFavouriteUseCase: DeleteFavouriteFavouriteUseCase
    private lateinit var fakeRepository: Repository

    @Before
    fun setUp()  = runTest{

        deleteFavouriteFavouriteUseCase = DeleteFavouriteFavouriteUseCase(fakeRepository)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    
    fun `delete favourite location from database should return not contained`()  = runTest(UnconfinedTestDispatcher()) {
        val location = FavouriteLocation(1,"القاهرة", "cairo", latitude = "1.0", longitude = "2.0")
        val response : Flow<Response<String>> =   fakeRepository.insertFavouriteLocation(location)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { response.collect()}
        val addResult = mutableListOf<Location>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
          addResult.addAll(fakeRepository.getFavourites().first())
        }
        assert(addResult.contains(location.toLocation()))
        backgroundScope.launch {  fakeRepository.deleteFavouriteLocation(latitude = "1.0", longitude = "2.0")}
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            addResult.clear()
            addResult.addAll(fakeRepository.getFavourites().first())
        }
        assert(!addResult.contains(location.toLocation()))
    }
}