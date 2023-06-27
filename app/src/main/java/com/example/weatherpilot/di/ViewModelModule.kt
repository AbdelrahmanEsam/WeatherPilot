package com.example.weatherpilot.di

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.GetCurrentDateUseCase
import com.example.weatherpilot.domain.usecase.GetWeatherDataUseCase
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


}