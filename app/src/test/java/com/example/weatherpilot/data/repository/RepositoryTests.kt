package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.SearchResponseItem
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.data.local.FakeLocalDataSourceImpl
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.remote.FakeRemoteDataSource
import com.example.weatherpilot.data.remote.RemoteDataSource
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTests {


    private lateinit var repository: Repository
    private lateinit var fakeLocalDataSource: LocalDataSource
    private lateinit var fakeRemoteDataSource: RemoteDataSource

    private val favourites: MutableList<FavouriteLocation> = mutableListOf(
        FavouriteLocation(
            id = 1,
            arabicName = "القاهرة",
            englishName = "cairo",
            longitude = "10.66",
            latitude = "15.66"
        ),
        FavouriteLocation(
            id = 1,
            arabicName = "الاسكندرية",
            englishName = "alexandria",
            longitude = "20.66",
            latitude = "20.66"
        ),
        FavouriteLocation(
            id = 1,
            arabicName = "الرياض",
            englishName = "riyadh",
            longitude = "30.66",
            latitude = "30.66"
        ),
        FavouriteLocation(
            id = 1,
            arabicName = "الدوحة",
            englishName = "doha",
            longitude = "40.66",
            latitude = "40.66"
        ),


        )
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
    private val dataStore: MutableMap<String, String?> = mutableMapOf()



    private val weatherItems : List<WeatherResponse> = mutableListOf(
        WeatherResponse(lat = 55.5, lon = 55.5),
        WeatherResponse(lat = 56.5, lon = 56.5),
        WeatherResponse(lat = 57.5, lon = 57.5),
        WeatherResponse(lat = 58.5, lon = 58.5),

        )

    private val searchItems : SearchResponseDto = SearchResponseDto().apply {
        addAll(listOf(
            SearchResponseItem(
            name = "القاهرة",
            lat = 30.66,
            lon = 30.66),
            SearchResponseItem(
            name = "الرياض",
            lat = 40.66,
            lon = 40.66),
            SearchResponseItem(
            name = "الدوحة",
            lat = 40.66,
            lon = 40.66),
            SearchResponseItem(
            name = "الاسكندرية",
            lat = 50.66,
            lon = 50.66),))
    }



    @Before
    fun setUp()  {
        fakeLocalDataSource = FakeLocalDataSourceImpl(favourites, alerts, dataStore)
        fakeRemoteDataSource = FakeRemoteDataSource(weatherItems, searchItems)
        repository = RepositoryImpl(fakeRemoteDataSource,fakeLocalDataSource)
    }

    @Test
    fun `get weather response from remote source with valid lat and long should return value`() =   runTest(UnconfinedTestDispatcher())
    {
            val item : Flow<Response<WeatherResponse>> = repository.getWeatherResponse(
                weatherItems.first().lat.toString(),
                weatherItems.first().lon.toString(),
                "en"
            )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { item.collect()}

        assertThat((item.first() as Response.Success ).data?.lat ,equalTo( weatherItems.first().lat))
        assertThat((item.first() as Response.Success ).data?.lon ,equalTo(weatherItems.first().lon))
    }


    @Test
    fun `get weather response from remote source with Invalid lat and long should return empty success value`() =   runTest(UnconfinedTestDispatcher())
    {
        val item : Flow<Response<WeatherResponse>> = repository.getWeatherResponse(
            "888",
            "888",
            "en"
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { item.collect()}

        assertThat((item.first() as Response.Success ).data ,equalTo(null))
    }


    @Test
    fun `get weather response from remote source with exception  should return failure with message error`() =   runTest(UnconfinedTestDispatcher())
    {

        (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnGeneralError(true)
        val item : Flow<Response<WeatherResponse>> = repository.getWeatherResponse(
            "888",
            "888",
            "en"
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { item.collect()}

        assertThat((item.first() as Response.Failure ).error ,equalTo("error"))
    }


    @Test
    fun `get weather response from remote source with  connection exception  should return failure with message Please check your network connection`() =   runTest(UnconfinedTestDispatcher())
    {

        (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnConnectionError(true)
        val item : Flow<Response<WeatherResponse>> = repository.getWeatherResponse(
            "888",
            "888",
            "en"
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { item.collect()}

        assertThat((item.first() as Response.Failure ).error ,equalTo("Please check your network connection"))
    }


    @Test
    fun `get search response from remote with valid name should return success with value`() =  runTest(UnconfinedTestDispatcher())
    {
        val searchResult : Flow<Response<SearchResponseItem>> = repository.getSearchResponse(searchItems.first().name!!)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { searchResult.collect()}
        assertThat((searchResult.first() as Response.Success ).data?.lat ,equalTo( searchItems.first().lat))
        assertThat((searchResult.first() as Response.Success ).data?.lon ,equalTo( searchItems.first().lon))
    }


    @Test
    fun `get search response from remote with invalid name should return success with empty value`() =  runTest(UnconfinedTestDispatcher())
    {
        val searchResult : Flow<Response<SearchResponseItem>> = repository.getSearchResponse("")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { searchResult.collect()}
        assertThat((searchResult.first() as Response.Success ).data ,equalTo(null))
    }


    @Test
    fun `get search response from remote with general exception to return  should return failure with  message error`() =  runTest(UnconfinedTestDispatcher())
    {
        (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnGeneralError(true)
        val searchResult : Flow<Response<SearchResponseItem>> = repository.getSearchResponse("")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { searchResult.collect()}
        assertThat((searchResult.first() as Response.Failure ).error ,equalTo("error"))
    }


    @Test
    fun `get search response from remote with connection exception name should return failure with message Please check your network connection`() =  runTest(UnconfinedTestDispatcher())
    {
        (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnConnectionError(true)
        val searchResult : Flow<Response<SearchResponseItem>> = repository.getSearchResponse("")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { searchResult.collect()}
        assertThat((searchResult.first() as Response.Failure ).error ,equalTo("Please check your network connection"))
    }

}