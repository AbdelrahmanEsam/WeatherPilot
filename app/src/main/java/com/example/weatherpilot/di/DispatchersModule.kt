package com.example.weatherpilot.di

import com.example.weatherpilot.util.Dispatcher
import com.example.weatherpilot.util.Dispatchers.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
        @Provides
        @Dispatcher(IO)
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @Dispatcher(Default)
        fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

        @Provides
        @Dispatcher(Main)
        fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        @Provides
        @Dispatcher(Unconfined)
        fun providesUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined


}