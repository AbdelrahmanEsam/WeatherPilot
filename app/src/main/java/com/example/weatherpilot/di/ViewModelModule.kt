package com.example.weatherpilot.di

import com.example.weatherpilot.data.remote.WeatherInterface
import com.example.weatherpilot.data.rempositoryImpl.RepositoryImpl
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.domain.usecase.GetCurrentTimeStampUseCase
import com.example.weatherpilot.domain.usecase.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.SaveStringToDataStoreUseCase
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
    fun providesGetCurrentTimeStampUseCase() : GetCurrentTimeStampUseCase
    = GetCurrentTimeStampUseCase()


    @Provides
    @ViewModelScoped
    fun providesSaveStringToDataStoreUseCase(repository: Repository) : SaveStringToDataStoreUseCase
    = SaveStringToDataStoreUseCase(repository)


}