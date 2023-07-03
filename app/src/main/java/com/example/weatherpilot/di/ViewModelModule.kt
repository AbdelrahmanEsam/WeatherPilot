package com.example.weatherpilot.di

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.GetAllAlertsUseCase
import com.example.weatherpilot.domain.usecase.GetAllFavouritesUseCase
import com.example.weatherpilot.domain.usecase.GetCurrentDateUseCase
import com.example.weatherpilot.domain.usecase.GetTimeStampUseCase
import com.example.weatherpilot.domain.usecase.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.InsertAlertUseCase
import com.example.weatherpilot.domain.usecase.InsertNewFavouriteUseCase
import com.example.weatherpilot.domain.usecase.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.domain.usecase.SaveStringToDataStoreUseCase
import com.example.weatherpilot.domain.usecase.TempTransformerUseCase
import com.example.weatherpilot.domain.usecase.WindSpeedTransformerUseCase
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
    fun providesGetAllAlerts(repository: Repository) : GetAllAlertsUseCase
            = GetAllAlertsUseCase(repository)


    @Provides
    @ViewModelScoped
    fun providesGetTimeStampUseCase() : GetTimeStampUseCase
            = GetTimeStampUseCase()


}