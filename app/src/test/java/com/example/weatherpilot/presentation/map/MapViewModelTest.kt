package com.example.weatherpilot.presentation.map

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.alerts.InsertAlertUseCase
import com.example.weatherpilot.domain.usecase.alerts.UpdateAlertUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.datastore.SaveStringToDataStoreUseCase
import com.example.weatherpilot.domain.usecase.favourites.InsertNewFavouriteUseCase
import com.example.weatherpilot.domain.usecase.network.SearchCityByNameUseCase
import com.example.weatherpilot.domain.usecase.transformers.GetTimeStampUseCase
import com.example.weatherpilot.util.DispatcherTestingRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

class MapViewModelTest{


    @get:Rule
    val mainDispatcherTestingRule = DispatcherTestingRule()

    private lateinit var repository: Repository
    private lateinit var saveStringToDataStoreUseCase: SaveStringToDataStoreUseCase
    private lateinit var insertNewFavouriteToDatabase: InsertNewFavouriteUseCase
    private lateinit var readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase
    private lateinit var insertAlertUseCase: InsertAlertUseCase
    private lateinit var getTimeStampUseCase: GetTimeStampUseCase
    private lateinit var updateAlertUseCase: UpdateAlertUseCase
    private lateinit var searchCityByNameUseCase: SearchCityByNameUseCase
    private lateinit var viewModel: MapViewModel


    @Before
    fun setUp()
    {

    }

}