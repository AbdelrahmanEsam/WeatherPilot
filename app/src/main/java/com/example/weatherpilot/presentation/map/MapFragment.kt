package com.example.weatherpilot.presentation.map

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class MapFragment(private val ioDispatcher: CoroutineDispatcher) : Fragment() {


    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private var previousDestination: String? = null

    private val viewModel: MapViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        previousDestination = arguments?.getString(getString(R.string.previous))
        googleMapHandler()


        binding.go.setOnClickListener {

            if (previousDestination == getString(R.string.from_favourite_fragment)) {
                viewModel.onEvent(MapIntent.SaveFavourite)

            } else {
                viewModel.onEvent(MapIntent.SaveDataToDataStore)

            }
            navController.popBackStack()
        }
        stateObserver()
    }

    private fun stateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    when (it.saveState) {
                        true -> {
                            navController.popBackStack()
                        }

                        false -> binding.progressBar.visibility = View.VISIBLE
                        null -> {}
                    }

                    when (it.mapLoadingState) {
                        true -> binding.progressBar.visibility = View.VISIBLE
                        false -> binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun googleMapHandler() {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->
            googleMap.setOnMapLongClickListener { latLong ->


                viewModel.onEvent(MapIntent.NewFavouriteLocation("", "", "", ""))


                if (previousDestination == getString(R.string.from_favourite_fragment)) {


                    getCityNameByLatLong(latLong, googleMap)
                } else {
                    setMarker(latLong,googleMap)
                    viewModel.onEvent(
                        MapIntent.NewLatLong(
                            latLong.latitude.toString(),
                            latLong.longitude.toString()
                        )
                    )
                }
            }

            googleMap.setOnMapLoadedCallback {
                viewModel.onEvent(MapIntent.MapLoaded)
                with(viewModel.state.value){
                        setMarker(LatLng(latitude.toDouble(),longitude.toDouble()),googleMap)

              }
            }

        }


    }

    private fun getCityNameByLatLong(
        latLong: LatLng,
        googleMap: GoogleMap
    ) {
        val englishGeoCoder = Geocoder(requireContext(), Locale.US)
        val arabicGeoCoder = Geocoder(requireContext(), Locale(getString(R.string.ar)))

        lifecycleScope.launch(ioDispatcher) {

            englishGeoCoder.getAddress(latLong.latitude, latLong.longitude)
                .combine(
                    arabicGeoCoder.getAddress(
                        latLong.latitude,
                        latLong.longitude
                    )
                ) { englishAddress, arabicAddress ->

                    if (englishAddress.getAddressLine(0).isEmpty()) return@combine
                    if (arabicAddress.getAddressLine(0).isEmpty()) return@combine

                    viewModel.onEvent(
                        MapIntent.NewFavouriteLocation(
                            arabicAddress.getAddressLine(
                                0
                            ), englishAddress.getAddressLine(
                                0
                            ), latLong.latitude.toString(),
                            latLong.longitude.toString()
                        )
                    )


                    withContext(Dispatchers.Main) {
                        setMarker(latLong,googleMap)
                    }

                }.collect()
        }

    }

    private fun setMarker(latLong : LatLng , googleMap: GoogleMap)
    {
            googleMap.apply {
                clear()
                addMarker(
                    MarkerOptions()
                        .position(latLong)
                )
            }

    }


    @Suppress("DEPRECATION")
    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
    ): Flow<Address> {

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(latitude, longitude, 1)?.asFlow() ?:  getAddress(latitude, longitude)
            }
            getFromLocation(latitude, longitude, 1)?.asFlow() ?:  getAddress(latitude, longitude)
        } catch (e: Exception) {
            getAddress(latitude, longitude)
        }
    }


}