package com.example.weatherpilot.di

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.alerts.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.alerts.GetAllAlertsUseCase
import com.example.weatherpilot.domain.usecase.favourites.GetAllFavouritesUseCase
import com.example.weatherpilot.domain.usecase.transformers.GetCurrentDateUseCase
import com.example.weatherpilot.domain.usecase.transformers.GetTimeStampUseCase
import com.example.weatherpilot.domain.usecase.network.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.alerts.InsertAlertUseCase
import com.example.weatherpilot.domain.usecase.favourites.InsertNewFavouriteUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.datastore.SaveStringToDataStoreUseCase
import com.example.weatherpilot.domain.usecase.network.SearchCityByNameUseCase
import com.example.weatherpilot.domain.usecase.transformers.TempTransformerUseCase
import com.example.weatherpilot.domain.usecase.alerts.UpdateAlertUseCase
import com.example.weatherpilot.domain.usecase.transformers.WindSpeedTransformerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun providesGetWeatherDataUseCase(repository: Repository) : GetWeatherDataUseCase
    = GetWeatherDataUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesGetCurrentTimeStampUseCase() : GetCurrentDateUseCase
    = GetCurrentDateUseCase()


    @Provides
    @ViewModelScoped
    fun providesSaveStringToDataStoreUseCase(repository: Repository) : SaveStringToDataStoreUseCase
    = SaveStringToDataStoreUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesReadStringToDataStoreUseCase(repository: Repository) : ReadStringFromDataStoreUseCase
            = ReadStringFromDataStoreUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesWindSpeedTransformerUseCase() : WindSpeedTransformerUseCase
            = WindSpeedTransformerUseCase()


    @Provides
    @ViewModelScoped
    fun providesTempTransformerUseCase() : TempTransformerUseCase
            = TempTransformerUseCase()


    @Provides
    @ViewModelScoped
    fun providesGetAllFavouritesUseCase(repository: Repository) : GetAllFavouritesUseCase
            = GetAllFavouritesUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providesInsertNewFavouriteToDatabase(repository: Repository) : InsertNewFavouriteUseCase
            = InsertNewFavouriteUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providesInsertAlertToDatabase(repository: Repository) : InsertAlertUseCase
            = InsertAlertUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesDeleteAlertToDatabase(repository: Repository) : DeleteAlertUseCase
            = DeleteAlertUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesUpdateAlertToDatabase(repository: Repository) : UpdateAlertUseCase
            = UpdateAlertUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesGetAllAlerts(repository: Repository) : GetAllAlertsUseCase
            = GetAllAlertsUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesGetTimeStampUseCase() : GetTimeStampUseCase
            = GetTimeStampUseCase()


    @Provides
    @ViewModelScoped
    fun providesSearchCityByNameUseCase(repository: Repository) : SearchCityByNameUseCase
            = SearchCityByNameUseCase(repository)

}