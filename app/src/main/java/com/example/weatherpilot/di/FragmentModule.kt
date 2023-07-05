package com.example.weatherpilot.di

import android.content.Context
import android.location.Geocoder
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.weatherpilot.R
import com.example.weatherpilot.util.hiltanotations.GeoCoder
import com.example.weatherpilot.util.hiltanotations.GeoCoders
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import java.util.Locale
import com.example.weatherpilot.util.hiltanotations.GeoCoders.*

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule{

    @Provides
    @FragmentScoped
    fun providesLocationRequestBuilder() = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,0).apply {
        setMinUpdateDistanceMeters(500f)
        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        setWaitForAccurateLocation(true)
    }.build()

    @Provides
    @FragmentScoped
    fun providesLocationClient(@ActivityContext context: Context) = LocationServices.getFusedLocationProviderClient(context)


    @Provides
    @FragmentScoped
    @GeoCoder(EnglishGeoCoder)
    fun providesEnglishGeoCoder(@ActivityContext context: Context) = Geocoder(context, Locale.US)


    @Provides
    @FragmentScoped
    @GeoCoder(ArabicGeoCoder)
    fun providesArabicGeoCoder(@ActivityContext context: Context) =  Geocoder(
        context,
        Locale(context.getString(R.string.ar))
    )

}