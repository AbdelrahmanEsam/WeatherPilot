package com.example.weatherpilot.presentation.map

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.data.dto.SearchResponseDto
import com.example.weatherpilot.data.dto.SearchResponseItem
import com.example.weatherpilot.data.mappers.toAlertItem
import com.example.weatherpilot.data.repository.FakeRepository
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.alerts.InsertAlertUseCase
import com.example.weatherpilot.domain.usecase.alerts.UpdateAlertUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.datastore.SaveStringToDataStoreUseCase
import com.example.weatherpilot.domain.usecase.favourites.InsertNewFavouriteUseCase
import com.example.weatherpilot.domain.usecase.network.SearchCityByNameUseCase
import com.example.weatherpilot.domain.usecase.transformers.GetTimeStampUseCase
import com.example.weatherpilot.util.DispatcherTestingRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {


    @get:Rule
    val mainDispatcherTestingRule = DispatcherTestingRule()

    private lateinit var repository: Repository
    private lateinit var saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase
    private lateinit var readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase
    private lateinit var insertNewFavouriteToDatabase: InsertNewFavouriteUseCase
    private lateinit var insertAlertUseCase: InsertAlertUseCase
    private lateinit var updateAlertUseCase: UpdateAlertUseCase
    private lateinit var getTimeStampUseCase: GetTimeStampUseCase
    private lateinit var searchCityByNameUseCase: SearchCityByNameUseCase
    private lateinit var viewModel: MapViewModel


    private val dataStore: MutableMap<String, String?> = mutableMapOf(
        Pair("languageType", "ar"),
        Pair("locationType", "gps"),
        Pair("windType", "M/S"),
        Pair("temperatureType", "F"),
        Pair("longitude", "55.5"),
        Pair("latitude", "55.5")
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
            id = 5,
            arabicName = "الدوحة",
            englishName = "doha", longitude = "40.66",
            latitude = "40.66",
            time = 1234567898854,
            message = "weather is bad",
            kind = "alert",
            scheduled = false
        ),
    )


    private val favourites: MutableList<FavouriteLocation> = mutableListOf()


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

    @Before
    fun setUp() {

        repository = FakeRepository(
            favourites = favourites,
            alerts = alerts,
            searchItems = searchItems,
            dataStore = dataStore
        )
        saveStringToDataStoreUseCase = SaveStringToDataStoreUseCase(repository)
        readStringFromDataStoreUseCase = ReadStringFromDataStoreUseCase(repository)
        insertNewFavouriteToDatabase = InsertNewFavouriteUseCase(repository)
        insertAlertUseCase = InsertAlertUseCase(repository)
        updateAlertUseCase = UpdateAlertUseCase(repository)
        getTimeStampUseCase = GetTimeStampUseCase()
        searchCityByNameUseCase = SearchCityByNameUseCase(repository)
        viewModel = MapViewModel(
            Dispatchers.Unconfined,
            saveStringToDataStoreUseCase,
            insertNewFavouriteToDatabase,
            readStringFromDataStoreUseCase,
            insertAlertUseCase,
            getTimeStampUseCase,
            updateAlertUseCase,
            searchCityByNameUseCase
        )
    }

    @Test
    fun `send ReadLatLongFromDataStore intent should update latLongStates with the expected values`() =
        runTest {

            backgroundScope.launch {
                viewModel.state.collect()
            }

            MatcherAssert.assertThat(
                viewModel.state.value.latitude,
                CoreMatchers.equalTo(dataStore["latitude"])
            )

            MatcherAssert.assertThat(
                viewModel.state.value.longitude,
                CoreMatchers.equalTo(dataStore["longitude"])
            )
        }


    @Test
    fun `send SaveLocationToDataStore intent should save lat and long to dataStore`() =
        runTest {
            val lat = "100.5"
            val long = "150.10"
            viewModel.onEvent(MapIntent.NewLatLong(latitude = lat, longitude = long))
            viewModel.onEvent(MapIntent.SaveLocationToDataStore)
            backgroundScope.launch {
                viewModel.state.collect()
            }

            MatcherAssert.assertThat(
                viewModel.state.value.latitude,
                CoreMatchers.equalTo(lat)
            )

            MatcherAssert.assertThat(
                viewModel.state.value.longitude,
                CoreMatchers.equalTo(long)
            )
        }

    @Test
    fun `send MapLoaded intent with regular fragment should change the regular state to loaded depend`() =
        runTest {
            MatcherAssert.assertThat(
                viewModel.state.value.mapLoadingState,
                CoreMatchers.equalTo(true)
            )
            viewModel.onEvent(MapIntent.MapLoaded("from regular fragment"))
            backgroundScope.launch {
                viewModel.state.collect()
            }

            MatcherAssert.assertThat(
                viewModel.state.value.mapLoadingState,
                CoreMatchers.equalTo(false)
            )

        }


    @Test
    fun `send MapLoaded intent with favourite fragment should change the favourite state to loaded depend`() =
        runTest {
            MatcherAssert.assertThat(
                viewModel.favouriteState.value.mapLoadingState,
                CoreMatchers.equalTo(true)
            )
            viewModel.onEvent(MapIntent.MapLoaded("from favourite Fragment"))
            backgroundScope.launch {
                viewModel.favouriteState.collect()
            }

            MatcherAssert.assertThat(
                viewModel.favouriteState.value.mapLoadingState,
                CoreMatchers.equalTo(false)
            )

        }


    @Test
    fun `send MapLoaded intent with any other text should change the alert state to loaded depend`() =
        runTest {
            MatcherAssert.assertThat(
                viewModel.alertState.value.mapLoadingState,
                CoreMatchers.equalTo(true)
            )
            viewModel.onEvent(MapIntent.MapLoaded(""))
            backgroundScope.launch {
                viewModel.alertState.collect()
            }

            MatcherAssert.assertThat(
                viewModel.alertState.value.mapLoadingState,
                CoreMatchers.equalTo(false)
            )

        }


    @Test
    fun `send NewFavouriteLocation intent should update the favourite state`() = runTest {
        val newLocation = com.example.weatherpilot.domain.model.Location(
            arabicName = "القاهرة",
            englishName = "cairo",
            latitude = "100.5",
            longitude = "50.5"
        )
        with(newLocation) {
            viewModel.onEvent(
                MapIntent.NewFavouriteLocation(
                    arabicName,
                    englishName,
                    latitude,
                    longitude
                )
            )
        }
        backgroundScope.launch {
            viewModel.favouriteState.collect()
        }
        MatcherAssert.assertThat(
            viewModel.favouriteState.value.arabicName,
            CoreMatchers.equalTo(newLocation.arabicName)
        )

        MatcherAssert.assertThat(
            viewModel.favouriteState.value.englishName,
            CoreMatchers.equalTo(newLocation.englishName)
        )

        MatcherAssert.assertThat(
            viewModel.favouriteState.value.latitude,
            CoreMatchers.equalTo(newLocation.latitude)
        )

        MatcherAssert.assertThat(
            viewModel.favouriteState.value.longitude,
            CoreMatchers.equalTo(newLocation.longitude)
        )

    }

    @Test
    fun `send SaveFavourite intent with valid location should save new favourite`() = runTest {

        val newLocation = com.example.weatherpilot.domain.model.Location(
            arabicName = "القاهرة",
            englishName = "cairo",
            latitude = "100.5",
            longitude = "50.5"
        )
        with(newLocation) {
            viewModel.onEvent(
                MapIntent.NewFavouriteLocation(
                    arabicName,
                    englishName,
                    latitude,
                    longitude
                )
            )
        }
        viewModel.onEvent(MapIntent.SaveFavourite)

        backgroundScope.launch {
            viewModel.favouriteState.collect()
        }

        MatcherAssert.assertThat(
            favourites.size,
            CoreMatchers.equalTo(1)
        )
    }


    @Test
    fun `send AlertLocationIntent intent should update the alert state`() = runTest {
        val alert = alerts.first().toAlertItem()
        with(alert) {
            viewModel.onEvent(
                MapIntent.AlertLocationIntent(
                    arabicName,
                    englishName,
                    latitude,
                    longitude
                )
            )
        }

        backgroundScope.launch {
            viewModel.alertState.collect()

        }
        MatcherAssert.assertThat(
            viewModel.alertState.value.arabicName,
            CoreMatchers.equalTo(alert.arabicName)
        )

        MatcherAssert.assertThat(
            viewModel.alertState.value.englishName,
            CoreMatchers.equalTo(alert.englishName)
        )

        MatcherAssert.assertThat(
            viewModel.alertState.value.latitude,
            CoreMatchers.equalTo(alert.latitude)
        )

        MatcherAssert.assertThat(
            viewModel.alertState.value.longitude,
            CoreMatchers.equalTo(alert.longitude)
        )

    }


    @Test
    fun `send SaveAlert intent with valid alert should save new Alert`() = runTest {
        val alert = alerts.first().copy(arabicName = "الدار البيضاء", englishName = "casablanca")
            .toAlertItem()
        with(alert) {
            viewModel.onEvent(
                MapIntent.AlertLocationIntent(
                    arabicName,
                    englishName,
                    latitude,
                    longitude
                )
            )
        }
        viewModel.onEvent(MapIntent.SaveAlert)

        backgroundScope.launch {
            viewModel.alertState.collect()
        }

        MatcherAssert.assertThat(
            alerts.size,
            CoreMatchers.equalTo(5)
        )
    }


    @Test
    fun `send SetAlarmDateIntent with new date should update the date`() = runTest {
        viewModel.onEvent(MapIntent.SetAlarmDateIntent("new date"))
        backgroundScope.launch {
            viewModel.alertState.collect()
        }
        MatcherAssert.assertThat(
            viewModel.alertState.value.date,
            CoreMatchers.equalTo("new date")
        )

    }


    @Test
    fun `send SetAlarmDateIntent with new time should update the date`() = runTest {
        viewModel.onEvent(MapIntent.SetAlarmTimeIntent("new date"))
        backgroundScope.launch {
            viewModel.alertState.collect()
        }
        MatcherAssert.assertThat(
            viewModel.alertState.value.time,
            CoreMatchers.equalTo("new date")
        )
    }

    @Test
    fun `send UpdateAlertStateToScheduled intent should update alert state to true`() = runTest {
        val alert = alerts.last().toAlertItem()

        viewModel.onEvent(MapIntent.UpdateAlertStateToScheduled(alert))

        MatcherAssert.assertThat(
            alerts.last().scheduled,
            CoreMatchers.equalTo(true)
        )

    }

    @Test
    fun `send SearchCityName intent  with  valid name should return valid result`() = runTest {
      val searchItemName =   searchItems.first().name!!
        viewModel.onEvent(MapIntent.SearchCityName(searchItemName))
        backgroundScope.launch {
            viewModel.searchResultState.collect()
        }


        MatcherAssert.assertThat(
            viewModel.searchResultState.value.searchResult?.searchResults?.first()?.name,
            CoreMatchers.equalTo(searchItemName)
        )
    }


    @Test
    fun `send SearchCityName intent  with  invalid name should return null result`() = runTest {
        val searchItemName =  "none"
        viewModel.onEvent(MapIntent.SearchCityName(searchItemName))
        backgroundScope.launch {
            viewModel.searchResultState.collect()
        }


        MatcherAssert.assertThat(
            viewModel.searchResultState.value.searchResult?.searchResults,
            CoreMatchers.equalTo(emptyList())
        )
    }


    @Test
    fun `send SearchCityName intent  with simulated error should update the loading state to false`() = runTest {
        val searchItemName =  "none"
        (repository as FakeRepository).setShouldReturnGeneralError(true)
        viewModel.onEvent(MapIntent.SearchCityName(searchItemName))
        backgroundScope.launch {
            viewModel.searchResultState.collect()
        }


        MatcherAssert.assertThat(
            viewModel.searchResultState.value.loading,
            CoreMatchers.equalTo(false)
        )
    }



    @Test
    fun `send ClearSearchList intent should clear search result state`() = runTest {
        viewModel.onEvent(MapIntent.ClearSearchList)
        backgroundScope.launch {
            viewModel.searchResultState.collect()
        }


        MatcherAssert.assertThat(
            viewModel.searchResultState.value.searchResult?.searchResults,
            CoreMatchers.equalTo(null)
        )
    }

}