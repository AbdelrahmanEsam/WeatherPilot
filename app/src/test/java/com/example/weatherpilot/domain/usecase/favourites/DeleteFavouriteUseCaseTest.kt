package com.example.weatherpilot.domain.usecase.favourites

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.DispatcherTestingRule
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.times

class DeleteFavouriteUseCaseTest {


    private lateinit var deleteFavouriteUseCase: DeleteFavouriteUseCase
    private lateinit var fakeRepository: Repository

    @get:Rule
    val mainDispatcherTestingRule = DispatcherTestingRule()

//    @Mock
//    private lateinit var fakeRepository: Repository
//    private lateinit var deleteFavouriteUseCase : DeleteFavouriteUseCase


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
            fakeRepository = FakeRepository(favourites = favourites)
        deleteFavouriteUseCase = DeleteFavouriteUseCase(fakeRepository)

//        fakeRepository  = Mockito.mock()
//        deleteFavouriteUseCase = DeleteFavouriteUseCase(fakeRepository)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test

    fun `delete favourite location from database should return flow with success value and not contained`()  = runTest{
        val location = favourites.first()
        val resultFlow = deleteFavouriteUseCase.execute(id = location.id)
        backgroundScope.launch(UnconfinedTestDispatcher()) {
          resultFlow.collectLatest {
              MatcherAssert.assertThat(
                  (it as Response.Success<String>).data,
                  equalTo("success")
              )
              MatcherAssert.assertThat(favourites.contains(location),equalTo(false))
              MatcherAssert.assertThat(favourites.size,equalTo(3))
         }
        }


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `execute function should call deleteFavouriteLocation function`() = runTest {
        val location = favourites.first()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            deleteFavouriteUseCase.execute(location.id)
        }
            Mockito.verify(fakeRepository, times(1)).deleteFavouriteLocation<String>(location.id)
    }

}