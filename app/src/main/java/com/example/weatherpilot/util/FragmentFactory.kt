package com.example.weatherpilot.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.weatherpilot.presentation.main.HomeFragment
import com.example.weatherpilot.presentation.map.MapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FragmentFactory  @Inject constructor(
private val locationClient : FusedLocationProviderClient,
private val locationRequest: LocationRequest,
@Dispatcher(Dispatchers.IO)private val ioDispatcher: CoroutineDispatcher,
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){

             HomeFragment::class.java.name ->{
                 HomeFragment(locationClient,locationRequest)
            }

            MapFragment::class.java.name ->
            {
                MapFragment(ioDispatcher)
            }
            else -> return super.instantiate(classLoader, className)

        }
    }


}