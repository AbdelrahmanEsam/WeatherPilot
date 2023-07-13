package com.example.weatherpilot.presentation.main

import com.example.weatherpilot.data.dto.Current
import com.example.weatherpilot.data.dto.LocalNames
import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.SearchResponseItem
import com.example.weatherpilot.data.dto.Weather
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.data.mappers.toWeatherModel
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.cached.GetCachedResponseUseCase
import com.example.weatherpilot.domain.usecase.cached.UpdateCityNameUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.favourites.GetWeatherFromRemoteResponseUseCase
import com.example.weatherpilot.domain.usecase.network.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.network.SearchCityByNameUseCase
import com.example.weatherpilot.domain.usecase.transformers.GetCurrentDateUseCase
import com.example.weatherpilot.domain.usecase.transformers.TempTransformerUseCase
import com.example.weatherpilot.domain.usecase.transformers.WindSpeedTransformerUseCase
import com.example.weatherpilot.util.DispatcherTestingRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {


    @get:Rule
    val mainDispatcherTestingRule = DispatcherTestingRule()


    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: Repository
    private lateinit var getWeatherDataUseCase: GetWeatherDataUseCase
    private lateinit var getCurrentDataUseCase: GetCurrentDateUseCase
    private lateinit var readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase
    private lateinit var windSpeedTransformerUseCase: WindSpeedTransformerUseCase
    private lateinit var tempTransformerUseCase: TempTransformerUseCase
    private lateinit var searchCityByNameUseCase: SearchCityByNameUseCase
    private lateinit var getCachedResponseUseCase: GetCachedResponseUseCase
    private lateinit var updateCityNameUseCase: UpdateCityNameUseCase
    private lateinit var getWeatherFromRemoteResponseUseCase : GetWeatherFromRemoteResponseUseCase


    private val dataStore: MutableMap<String, String?> = mutableMapOf(
        Pair("languageType", "ar"),
        Pair("locationType", "gps"),
        Pair("windType", "M/S"),
        Pair("temperatureType", "F"),
        Pair("longitude", "55.5"),
        Pair("latitude", "55.5")
    )


    private val weatherItems: List<WeatherResponse> = mutableListOf(
        WeatherResponse(
            current = Current(
                pressure = 20,
                weather = listOf(Weather(id = 1, icon = "123", description = "sunny", main = ""))
            ), lat = 55.5, lon = 55.5, timezone = "egypt/cairo"
        ),
        WeatherResponse(
            current = Current(
                pressure = 50,
                weather = listOf(Weather(id = 2, icon = "1235", description = "rainy", main = ""))
            ), lat = 15.5, lon = 113.5, timezone = "egypt/alex"
        ),
        WeatherResponse(
            current = Current(
                pressure = 30,
                weather = listOf(Weather(id = 3, icon = "128", description = "cloudy", main = ""))
            ), lat = 155.5, lon = 20.5, timezone = "egypt/cairo"
        ),
        WeatherResponse(
            current = Current(
                pressure = 40,
                weather = listOf(Weather(id = 4, icon = "1239", description = "sunny", main = ""))
            ), lat = 100.5, lon = 15.5, timezone = "egypt/cairo"
        ),
        )


    private val searchItems: SearchResponseDto = SearchResponseDto().apply {
        addAll(
            listOf(
                SearchResponseItem(
                    name = "cairo",
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
        repository = FakeRepository(weatherItems = weatherItems, dataStore = dataStore, searchItems = searchItems, cachedWeather = weatherResponse)
        getWeatherDataUseCase = GetWeatherDataUseCase(repository)
        getCurrentDataUseCase = GetCurrentDateUseCase()
        readStringFromDataStoreUseCase = ReadStringFromDataStoreUseCase(repository)
        windSpeedTransformerUseCase = WindSpeedTransformerUseCase()
        tempTransformerUseCase = TempTransformerUseCase()
        searchCityByNameUseCase = SearchCityByNameUseCase(repository)
        getCachedResponseUseCase = GetCachedResponseUseCase(repository)
        updateCityNameUseCase = UpdateCityNameUseCase(repository)
        getWeatherFromRemoteResponseUseCase = GetWeatherFromRemoteResponseUseCase(repository)
        viewModel = HomeViewModel(
            ioDispatcher = kotlinx.coroutines.Dispatchers.Unconfined,
            getWeatherDataUseCase,
            getCurrentDataUseCase,
            readStringFromDataStoreUseCase,
            windSpeedTransformerUseCase,
            tempTransformerUseCase,
            searchCityByNameUseCase,
            getCachedResponseUseCase,
            updateCityNameUseCase,
            getWeatherFromRemoteResponseUseCase
        )


    }


    @Test
    fun `send ReadLatLongFromDataStore intent should update latLongStates with the expected values`() =
        runTest {
            viewModel.onEvent(HomeIntent.ReadLatLongFromDataStore)
            backgroundScope.launch {
                viewModel.stateLongLat.collect()
            }

            MatcherAssert.assertThat(
                viewModel.stateLongLat.value.latitude,
                CoreMatchers.equalTo(dataStore["latitude"])
            )

            MatcherAssert.assertThat(
                viewModel.stateLongLat.value.longitude,
                CoreMatchers.equalTo(dataStore["longitude"])
            )
        }

    @Test
    fun `send fetchData intent should update the UI state with the server data`() = runTest {


            viewModel.onEvent(HomeIntent.ReadLatLongFromDataStore)
            viewModel.onEvent(HomeIntent.FetchData)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.stateDisplay.collect()
            viewModel.stateLongLat.collect()
        }

        MatcherAssert.assertThat(
            viewModel.stateLongLat.value.latitude,
            CoreMatchers.equalTo(dataStore["latitude"])
        )

        MatcherAssert.assertThat(
            viewModel.stateLongLat.value.longitude,
            CoreMatchers.equalTo(dataStore["longitude"])
        )

        MatcherAssert.assertThat(
            weatherItems.first {
                it.lat == dataStore["latitude"]!!.toDouble()
                        && it.lon == dataStore["longitude"]!!.toDouble()

            }.current.pressure, CoreMatchers.equalTo(
               weatherResponse.first().current.pressure
            )
        )
    }


    @Test
    fun `send fetchData intent with invalid lat and long  should return null`() = runTest {

       viewModel.onEvent(HomeIntent.FetchData)

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.stateDisplay.collect()
        }


        MatcherAssert.assertThat(
            viewModel.stateLongLat.value.latitude,
            CoreMatchers.equalTo(null)
        )

        MatcherAssert.assertThat(
            viewModel.stateLongLat.value.longitude,
            CoreMatchers.equalTo(null)
        )

        MatcherAssert.assertThat(
            viewModel.stateDisplay.value.pressure?.toInt(), CoreMatchers.equalTo(
                null
            )
        )


    }


    @Test
    fun `send fetchData intent with empty latLng should return loading false and error invalid location`() =
        runTest {
                viewModel.onEvent(HomeIntent.FetchData)
            backgroundScope.launch {
                 viewModel.stateDisplay.collect()
            }


            MatcherAssert.assertThat(
                viewModel.stateDisplay.value.loading, CoreMatchers.equalTo(
                    false
                )
            )

            MatcherAssert.assertThat(
                viewModel.stateDisplay.value.error, CoreMatchers.equalTo(
                    "invalid location"
                )
            )
        }

    @Test
    fun `send FetchDataOfFavouriteLocation intent should fetch the data for the location and update the display state`() =
        runTest {
            viewModel.onEvent(
                HomeIntent.FetchDataOfFavouriteLocation(
                    dataStore["latitude"]!!,
                    dataStore["longitude"]!!
                )
            )


            backgroundScope.launch {
               viewModel.stateLongLat.collect()
                viewModel.stateDisplay.collect()
            }


            MatcherAssert.assertThat(
                viewModel.stateLongLat.value.latitude,
                CoreMatchers.equalTo(dataStore["latitude"])
            )

            MatcherAssert.assertThat(
                viewModel.stateLongLat.value.longitude,
                CoreMatchers.equalTo(dataStore["longitude"])
            )

            MatcherAssert.assertThat(
                viewModel.stateDisplay.value.pressure?.toInt(), CoreMatchers.equalTo(
                    weatherItems.first().current.pressure
                )
            )
        }

    @Test
    fun `send ReadPrefsFromDataStore  viewModel should read all values from datastore and update the prefs state`() =
        runTest {
            viewModel.onEvent(HomeIntent.ReadPrefsFromDataStore)
            backgroundScope.launch {
                viewModel.statePreferences.collect()
            }

            HomeState.Preferences::class.java.declaredFields.forEach { field ->
                field.isAccessible = true
                MatcherAssert.assertThat(
                    dataStore[field.name],
                    CoreMatchers.equalTo(field.get(viewModel.statePreferences.value))
                )
            }
        }



    @Test
    fun `get cached weather should update the display state with the cached`() = runTest(UnconfinedTestDispatcher()) {
        val resultFlow =  repository.getCachedWeatherFromDatabase()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            resultFlow.collectLatest {
                MatcherAssert.assertThat(
                    it.first(),
                    CoreMatchers.equalTo(weatherResponse.first().toWeatherModel())
                )
            }
        }
    }
}