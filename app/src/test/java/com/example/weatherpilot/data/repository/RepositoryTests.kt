package com.example.weatherpilot.data.repository

import com.example.weatherpilot.data.dto.Current
import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.SearchResponseItem
import com.example.weatherpilot.data.dto.Weather
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.data.local.FakeLocalDataSourceImpl
import com.example.weatherpilot.data.local.LocalDataSource
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.remote.FakeRemoteDataSource
import com.example.weatherpilot.data.remote.RemoteDataSource
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

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
    private val dataStore: MutableMap<String, String?> = mutableMapOf(
        Pair("languageType", "ar"),
        Pair("locationType", "gps"),
        Pair("windType", ""),
    )


    private val weatherItems: List<WeatherResponse> = mutableListOf(
        WeatherResponse(current = Current(), lat = 55.5, lon = 55.5),
        WeatherResponse(current = Current(), lat = 56.5, lon = 56.5),
        WeatherResponse(current = Current(), lat = 57.5, lon = 57.5),
        WeatherResponse(current = Current(), lat = 58.5, lon = 58.5),

        )

    private val searchItems: SearchResponseDto = SearchResponseDto().apply {
        addAll(
            listOf(
                SearchResponseItem(
                    name = "القاهرة",
                    lat = 30.66,
                    lon = 30.66
                ),
                SearchResponseItem(
                    name = "الرياض",
                    lat = 40.66,
                    lon = 40.66
                ),
                SearchResponseItem(
                    name = "الدوحة",
                    lat = 40.66,
                    lon = 40.66
                ),
                SearchResponseItem(
                    name = "الاسكندرية",
                    lat = 50.66,
                    lon = 50.66
                ),
            )
        )
    }

    private val weatherResponse = mutableListOf(WeatherResponse(current = Current(visibility = 15, weather = listOf(Weather(id = 0, description = "", main = "",icon = "1"))), lat = 55.5, lon = 55.5, timezone = "any/city"))


    @Before
    fun setUp() {
        fakeLocalDataSource = FakeLocalDataSourceImpl(favourites, alerts,weatherResponse,dataStore)
        fakeRemoteDataSource = FakeRemoteDataSource(weatherItems, searchItems)
        repository = RepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun `get weather response from remote source with valid lat and long should return succees value`() =
        runTest(UnconfinedTestDispatcher())
        {
            val item: Flow<Response<Int>> = repository.getWeatherResponse(
                weatherItems.first().lat.toString(),
                weatherItems.first().lon.toString(),
                "en"
            )
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                item.collect {


                    assertThat(
                        (item.first() as Response.Success).data,
                        equalTo("success")
                    )
                }
            }
        }


    @Test
    fun `get weather response from cached source should return valid value`() =
        runTest(UnconfinedTestDispatcher())
        {
            val item: Flow<List<WeatherModel>> = repository.getCachedWeatherFromDatabase()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                item.collect {
                    assertThat(it.first().visibility, equalTo(15))
                }
            }
        }


    @Test
    fun `get weather response from remote source with exception  should return failure with message error`() =
        runTest(UnconfinedTestDispatcher())
        {

            (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnGeneralError(true)
            val item: Flow<Response<WeatherResponse>> = repository.getWeatherResponse(
                "888",
                "888",
                "en"
            )

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                item.collect {


                    assertThat((item.first() as Response.Failure).error, equalTo("unknown error"))
                }
            }
        }


    @Test
    fun `get weather response from remote source with  connection exception  should return failure with message Please check your network connection`() =
        runTest(UnconfinedTestDispatcher())
        {

            (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnConnectionError(true)
            val item: Flow<Response<WeatherResponse>> = repository.getWeatherResponse(
                "888",
                "888",
                "en"
            )

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                item.collect {
                    assertThat(
                        (item.first() as Response.Failure).error,
                        equalTo("Please check your network connection")
                    )
                }
            }
        }


    @Test
    fun `get search response from remote with valid name should return success with value`() =
        runTest(UnconfinedTestDispatcher())
        {
            val searchResult: Flow<Response<SearchResponseItem>> =
                repository.getSearchResponse(searchItems.first().name!!)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                searchResult.collect {

                    assertThat(
                        (searchResult.first() as Response.Success).data?.lat,
                        equalTo(searchItems.first().lat)
                    )
                    assertThat(
                        (searchResult.first() as Response.Success).data?.lon,
                        equalTo(searchItems.first().lon)
                    )
                }
            }
        }


    @Test
    fun `get search response from remote with invalid name should return success with empty value`() =
        runTest(UnconfinedTestDispatcher())
        {
            val searchResult: Flow<Response<SearchResponseItem>> = repository.getSearchResponse("")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                searchResult.collect {
                    assertThat((it as Response.Success).data, equalTo(null))
                }
            }

        }


    @Test
    fun `get search response from remote with general exception to return  should return failure with  message error`() =
        runTest(UnconfinedTestDispatcher())
        {
            (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnGeneralError(true)
            val searchResult: Flow<Response<SearchResponseItem>> = repository.getSearchResponse("")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                searchResult.collect {
                    assertThat((it as Response.Failure).error, equalTo("unknown error"))
                }
            }

        }


    @Test
    fun `get search response from remote with connection exception name should return failure with message Please check your network connection`() =
        runTest(UnconfinedTestDispatcher())
        {
            (fakeRemoteDataSource as FakeRemoteDataSource).setShouldReturnConnectionError(true)
            val searchResult: Flow<Response<SearchResponseItem>> = repository.getSearchResponse("")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                searchResult.collect {
                    assertThat(
                        (it as Response.Failure).error,
                        equalTo("Please check your network connection")
                    )
                }
            }

        }


    @Test
    fun `add value with valid key  to dataStore should return flow of response with success`() =
        runTest(UnconfinedTestDispatcher())
        {

            val saveDataStoreResult = fakeLocalDataSource.saveStringToDataStore<String>(
                key = "new key",
                value = "new value"
            )
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                saveDataStoreResult.collect {
                    assertThat((it as Response.Success<String>).data, equalTo("success"))
                    assertThat(dataStore.size, equalTo(4))
                }
            }

        }


    @Test
    fun `add value with  invalid key  to dataStore should return flow of response with error`() =
        runTest(UnconfinedTestDispatcher())
        {

            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)
            val saveDataStoreResult =
                fakeLocalDataSource.getStringFromDataStore<String>(key = "notification")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                saveDataStoreResult.collect {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(dataStore.size, equalTo(3))
                }
            }

        }


    @Test
    fun `add value with  valid key and empty value to dataStore should return flow of response with error`() =
        runTest(UnconfinedTestDispatcher())
        {

            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)
            val saveDataStoreResult =
                fakeLocalDataSource.getStringFromDataStore<String>(key = "windType")
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                saveDataStoreResult.collect {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(dataStore.size, equalTo(3))
                }
            }

        }


    //favourites tests

    @Test
    fun `get all favourites from the local data source should the same 4 items of the fake list`() =
        runTest(UnconfinedTestDispatcher())
        {
            val favouritesFlow = fakeLocalDataSource.getFavourites()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                favouritesFlow.collectLatest {
                    assertThat(favourites.size, equalTo(4))
                }
            }
        }


    @Test
    fun `add new favourites to the localDataSource should return flow with success message`() =
        runTest(UnconfinedTestDispatcher())
        {

           val newItem =  favourites.first().copy(id = 5)
            assertThat(favourites.contains(newItem) , equalTo(false))
            val favouritesFlow = fakeLocalDataSource.insertFavouriteLocation<String>(newItem)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                favouritesFlow.collectLatest {
                    assertThat((it as Response.Success<String>).data, equalTo("success"))
                    assertThat(favourites.size, equalTo(5))
                    assertThat(favourites.contains(newItem) , equalTo(true))
                }
            }
        }


    @Test
    fun `add new favourite to the localDataSource with simulated general exception should return flow with error message`() =
        runTest(UnconfinedTestDispatcher())
        {
            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)

            val alertsFlow = fakeLocalDataSource.insertFavouriteLocation<String>(favourites.first())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(favourites.size, equalTo(4))
                }
            }
        }


    @Test
    fun `delete favourite from the localDataSource with should return flow with success message`() =
        runTest(UnconfinedTestDispatcher())
        {
          val favourite =   favourites.first()
            assertThat(favourites.contains(favourite) , equalTo(true))
            val favouritesFlow = fakeLocalDataSource.deleteFavouriteLocation<String>(favourite.id)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                favouritesFlow.collectLatest {
                    assertThat((it as Response.Success<String>).data, equalTo("success"))
                    assertThat(favourites.size, equalTo(3))
                    assertThat(favourites.contains(favourite) , equalTo(false))
                }
            }
        }


    @Test
    fun `delete favourite from the localDataSource with simulated exception should return flow with error message`() =
        runTest(UnconfinedTestDispatcher())
        {


            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)
            val favouritesFlow = fakeLocalDataSource.deleteFavouriteLocation<String>(favourites.first().id)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                favouritesFlow.collectLatest {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(favourites.size, equalTo(4))
                    assertThat(favourites.contains(favourites.first()) , equalTo(true))
                }
            }
        }







    @Test
    fun `get all alerts from the local data source should return the same 4 items of the fake list`() =
        runTest(UnconfinedTestDispatcher())
        {
            val alertsFlow = fakeLocalDataSource.getAlerts()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat(alerts.size, equalTo(4))
                }
            }
        }





    @Test
    fun `add new alert to the localDataSource should return flow with success message`() =
        runTest(UnconfinedTestDispatcher())
        {
            val alertsFlow = fakeLocalDataSource.insertAlertToDatabase<String>(alerts.first())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Success<String>).data, equalTo("success"))
                    assertThat(alerts.size, equalTo(5))
                }
            }
        }


    @Test
    fun `add new alert to the localDataSource with simulated general exception should return flow with error message`() =
        runTest(UnconfinedTestDispatcher())
        {
            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)

            val alertsFlow = fakeLocalDataSource.insertAlertToDatabase<String>(alerts.first())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(alerts.size, equalTo(4))
                }
            }
        }


    @Test
    fun `delete alert from the localDataSource  should return flow with success message`() =
        runTest(UnconfinedTestDispatcher())
        {

            val alert =  alerts.first()
            assertThat(alerts.contains(alert) , equalTo(true))
            val alertsFlow = fakeLocalDataSource.deleteAlertFromDatabase<String>(alerts.first())
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Success<String>).data, equalTo("success"))
                    assertThat(alerts.size, equalTo(3))
                    assertThat(alerts.contains(alert) , equalTo(false))
                }
            }
        }


    @Test
    fun `delete alert from the localDataSource with simulated exception should return flow with error message`() =
        runTest(UnconfinedTestDispatcher())
        {

           val alert =  alerts.first()
            assertThat(alerts.contains(alert) , equalTo(true))
            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)
            val alertsFlow = fakeLocalDataSource.deleteAlertFromDatabase<String>(alert)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(alerts.size, equalTo(4))
                    assertThat(alerts.contains(alert) , equalTo(true))
                }
            }
        }


    @Test
    fun `update alert from the localDataSource should return success message`() =
        runTest(UnconfinedTestDispatcher())
        {
            val alert = alerts.first()
            assertThat(alert.arabicName , equalTo("القاهرة"))
            val alertsFlow = fakeLocalDataSource.updateAlert<String>(alert.copy(arabicName = "المنصورة"))
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Success<String>).data, equalTo("success"))
                    assertThat(alerts.first {  it.time == alert.time }.arabicName , equalTo("المنصورة"))
                }
            }
        }


    @Test
    fun `update alert from the localDataSource with general exception should return success message`() =
        runTest(UnconfinedTestDispatcher())
        {
            val alert = alerts.first()
            assertThat(alert.arabicName , equalTo("القاهرة"))
            (fakeLocalDataSource as FakeLocalDataSourceImpl).setShouldReturnGeneralError(true)
            val alertsFlow = fakeLocalDataSource.updateAlert<String>(alert.copy(arabicName = "المنصورة"))
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                alertsFlow.collectLatest {
                    assertThat((it as Response.Failure<String>).error, equalTo("error"))
                    assertThat(alerts.first {  it.time == alert.time }.arabicName , equalTo("القاهرة"))
                }
            }
        }

    @Test
    fun `get cached weather from local database should return the last response`() = runTest(UnconfinedTestDispatcher()) {
       val resultFlow =  repository.getCachedWeatherFromDatabase()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            resultFlow.collectLatest {
                assertThat(it.first(), equalTo(weatherResponse.first().toWeatherModel()))
            }
        }
    }






}