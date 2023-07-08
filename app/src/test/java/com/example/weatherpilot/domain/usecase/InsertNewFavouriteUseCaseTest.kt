package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.mappers.toFavouriteLocation
import com.example.weatherpilot.data.mappers.toLocation
import com.example.weatherpilot.domain.model.Location
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

class InsertNewFavouriteUseCaseTest{


//    private lateinit var insertNewFavouriteUseCase: InsertNewFavouriteUseCase
//    private lateinit var fakeRepository: Repository


    private  var fakeRepository: Repository = Mockito.mock()
    private  var  insertNewFavouriteUseCase = InsertNewFavouriteUseCase(fakeRepository)
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
    fun setUp()  = runTest{

//        fakeRepository = FakeRepository(favourites = favourites)


    }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `insert items to database should return success string`()  = runTest(UnconfinedTestDispatcher()) {
//        val newItem =  favourites.first().copy(id = 5)
//        MatcherAssert.assertThat(favourites.contains(newItem), CoreMatchers.equalTo(false))
//        val favouritesFlow = fakeRepository.insertFavouriteLocation<String>(newItem)
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//            favouritesFlow.collectLatest {
//                MatcherAssert.assertThat(
//                    (it as Response.Success<String>).data,
//                    CoreMatchers.equalTo("success")
//                )
//                MatcherAssert.assertThat(favourites.size, CoreMatchers.equalTo(5))
//                MatcherAssert.assertThat(favourites.contains(newItem), CoreMatchers.equalTo(true))
//            }
//        }
//    }
//
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `insert items to database with simulated exception should return failure string`()  = runTest(UnconfinedTestDispatcher()) {
//        (fakeRepository as FakeRepository).setShouldReturnGeneralError(true)
//
//        val alertsFlow = fakeRepository.insertFavouriteLocation<String>(favourites.first())
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//            alertsFlow.collectLatest {
//                MatcherAssert.assertThat(
//                    (it as Response.Failure<String>).error,
//                    CoreMatchers.equalTo("error")
//                )
//                MatcherAssert.assertThat(favourites.size, CoreMatchers.equalTo(4))
//            }
//        }
//    }

       @OptIn(ExperimentalCoroutinesApi::class)
       @Test
       fun `execute function in insert new use case should call insert new favourite from repo`()  = runTest(UnconfinedTestDispatcher()) {
           val location = favourites.first().toLocation()
           backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
               insertNewFavouriteUseCase.execute( location)
               Mockito.verify(fakeRepository, times(1)).insertFavouriteLocation<FavouriteLocation>(location.toFavouriteLocation())
           }
    }
}