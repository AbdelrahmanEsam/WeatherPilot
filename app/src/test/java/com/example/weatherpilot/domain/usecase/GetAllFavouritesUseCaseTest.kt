package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.mappers.toFavouriteLocation
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.Mockito
import org.mockito.kotlin.times

class GetAllFavouritesUseCaseTest {

//    private lateinit var getAllFavouriteUseCase: GetAllFavouritesUseCase
//    private lateinit var fakeRepository: Repository

    private lateinit var getAllFavouriteUseCase: GetAllFavouritesUseCase
    private  var fakeRepository: Repository = Mockito.mock()

    private val favourites: MutableList<FavouriteLocation> = mutableListOf(
        FavouriteLocation(
            id = 1,
            arabicName = "القاهرة",
            englishName = "cairo",
            longitude = "10.66",
            latitude = "15.66"
        ),
        FavouriteLocation(
            id = 2,
            arabicName = "الاسكندرية",
            englishName = "alexandria",
            longitude = "20.66",
            latitude = "20.66"
        ),
        FavouriteLocation(
            id = 3,
            arabicName = "الرياض",
            englishName = "riyadh",
            longitude = "30.66",
            latitude = "30.66"
        ),
        FavouriteLocation(
            id = 4,
            arabicName = "الدوحة",
            englishName = "doha",
            longitude = "40.66",
            latitude = "40.66"
        ),


        )

    @Before
    fun setUp() = runTest {

//        fakeRepository = FakeRepository(favourites = favourites)
//        getAllFavouriteUseCase = GetAllFavouritesUseCase(fakeRepository)

        getAllFavouriteUseCase = GetAllFavouritesUseCase(fakeRepository)
    }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `get all locations from database should return the same number of items inserted to it`() =
//        runTest(UnconfinedTestDispatcher()) {
//
//            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//             getAllFavouriteUseCase.execute().collect{
//                 assertThat(favourites.size, CoreMatchers.equalTo(4))
//             }
//            }
//        }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `call execute fun function from use case should call the getAll function in repo`() =   runTest(UnconfinedTestDispatcher()) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
           getAllFavouriteUseCase.execute()
            Mockito.verify(fakeRepository, times(1)).getFavourites()
        }
    }
}