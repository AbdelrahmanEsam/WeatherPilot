package com.example.weatherpilot.di

import android.content.Context
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule{

    @Provides
    fun providesLocationRequestBuilder() = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,0).apply {
        setMinUpdateDistanceMeters(500f)
        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        setWaitForAccurateLocation(true)
    }.build()

    @Provides
    fun providesLocationClient(@ActivityContext context: Context) = LocationServices.getFusedLocationProviderClient(context)

}