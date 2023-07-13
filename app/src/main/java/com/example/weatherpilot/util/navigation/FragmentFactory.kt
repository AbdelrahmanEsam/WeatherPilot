package com.example.weatherpilot.util.navigation

import android.location.Geocoder
import android.location.LocationManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.presentation.favourites.FavouritesFragment
import com.example.weatherpilot.presentation.main.HomeFragment
import com.example.weatherpilot.presentation.map.MapFragment
import com.example.weatherpilot.presentation.notification.NotificationsFragment
import com.example.weatherpilot.presentation.splash.SplashFragment
import com.example.weatherpilot.util.alarm.AlarmSchedulerInterface
import com.example.weatherpilot.util.connectivity.ConnectivityObserver
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import com.example.weatherpilot.util.hiltanotations.GeoCoder
import com.example.weatherpilot.util.hiltanotations.GeoCoders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FragmentFactory  @Inject constructor(
    private val locationClient : FusedLocationProviderClient,
    private val locationManager: LocationManager,
    private val alarmScheduler: AlarmSchedulerInterface,
    private val locationRequest: LocationRequest,
    private val dataStoreUserPreferences: DataStoreUserPreferences,
    @Dispatcher(Dispatchers.IO)private val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(Dispatchers.Main)private val mainDispatcher: CoroutineDispatcher,
    @GeoCoder (GeoCoders.EnglishGeoCoder) private val englishGeoCoder: Geocoder,
    @GeoCoder (GeoCoders.ArabicGeoCoder) private val arabicGeoCoder: Geocoder,
    private val connectivityObserver: ConnectivityObserver,

    ): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){

             HomeFragment::class.java.name ->{
                 HomeFragment(locationClient,locationRequest,locationManager,connectivityObserver,ioDispatcher,mainDispatcher)
            }

            MapFragment::class.java.name ->
            {
                MapFragment(ioDispatcher,englishGeoCoder,arabicGeoCoder,connectivityObserver)
            }

            SplashFragment::class.java.name ->
            {
                SplashFragment(dataStoreUserPreferences,ioDispatcher)
            }

            NotificationsFragment::class.java.name -> {
                NotificationsFragment(alarmScheduler)
            }

            FavouritesFragment::class.java.name ->
            {
                FavouritesFragment(connectivityObserver)
            }
            else -> return super.instantiate(classLoader, className)

        }
    }


}