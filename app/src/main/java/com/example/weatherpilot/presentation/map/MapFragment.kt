package com.example.weatherpilot.presentation.map

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
        decideStateObserver()

        googleMapHandler()


        binding.go.setOnClickListener {
            if (previousDestination == getString(R.string.from_favourite_fragment)) {
                viewModel.onEvent(MapIntent.SaveFavourite)
            } else {
                viewModel.onEvent(MapIntent.SaveDataToDataStore)
            }
        }
    }

    private fun decideStateObserver() {
        if (previousDestination.isNullOrBlank()) {

            regularStateObserver()
        } else {
            favouriteStateObserver()
        }
        snackBarObserver()
    }

    private fun regularStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collectLatest {
                        Log.d("observer",it.saveState.toString())
                    when (it.saveState) {
                        true -> {
                            navController.navigate(NavGraphDirections.actionToHomeFragment(null))
                        }

                        false -> binding.progressBar.visibility = View.VISIBLE
                        null -> {}
                    }

                    when (it.mapLoadingState) {
                        true -> binding.progressBar.visibility = View.VISIBLE
                        false -> binding.progressBar.visibility = View.GONE
                    }

                    if (it.insertDataToDataStore == true) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }


    private fun favouriteStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.favouriteState.collect {
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

                    if (it.insertFavouriteResult == true) navController.popBackStack()
                }
            }
        }
    }


    private fun snackBarObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.snackBarFlow.collectLatest { errorMessage ->
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                        .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.baby_blue
                            )
                        )
                        .show()
                }
            }
        }
    }


    private fun googleMapHandler() {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->


            googleMap.setOnMapLongClickListener { latLong ->


                if (previousDestination == getString(R.string.from_favourite_fragment)) {

                    viewModel.onEvent(MapIntent.NewFavouriteLocation("", "", "", ""))
                    getCityNameByLatLong(latLong, googleMap)
                } else {
                    setMarker(latLong, googleMap)
                    viewModel.onEvent(
                        MapIntent.NewLatLong(
                            latLong.latitude.toString(),
                            latLong.longitude.toString()
                        )
                    )
                    Log.d("markerListener", viewModel.state.value.longitude.toString())
                }
            }

            googleMap.setOnMapLoadedCallback {
                viewModel.onEvent(
                    MapIntent.MapLoaded(
                        previousDestination ?: getString(R.string.from_regular_fragment)
                    )
                )
                if (previousDestination.isNullOrBlank()) {
                    with(viewModel.state.value) {
                        latitude?.let {
                            setMarker(
                                LatLng(latitude.toDouble(), longitude!!.toDouble()),
                                googleMap
                            )
                        }
                    }
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



        englishGeoCoder.getAddress(latLong.latitude, latLong.longitude) { englishFlow ->
            arabicGeoCoder.getAddress(
                latLong.latitude,
                latLong.longitude
            ) { arabicFlow ->
                lifecycleScope.launch(ioDispatcher) {
                    englishFlow.combine(arabicFlow) { englishAddress, arabicAddress ->


                        if (englishAddress == null) return@combine
                        if (arabicAddress == null) return@combine

                        viewModel.onEvent(
                            MapIntent.NewFavouriteLocation(
                                if (!arabicAddress.locality.isNullOrBlank()) arabicAddress.locality else arabicAddress.countryName,
                                if (!englishAddress.locality.isNullOrBlank()) englishAddress.locality else englishAddress.countryName,
                                latLong.latitude.toString(),
                                latLong.longitude.toString()
                            )
                        )


                        withContext(Dispatchers.Main) {
                            setMarker(latLong, googleMap)
                        }

                    }.collect()
                }
            }

        }


    }

    private fun setMarker(latLong: LatLng, googleMap: GoogleMap) {
        googleMap.apply {
            clear()
            addMarker(
                MarkerOptions()
                    .position(latLong)
            )
        }

    }


    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (Flow<Address?>) -> Unit
    ) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(latitude, longitude, 1) {
                    address(flowOf(it[0]))
                }
            } else {
                address(flowOf(getFromLocation(latitude, longitude, 1)?.get(0)))
            }
        } catch (e: Exception) {
            address(emptyFlow())
        }
    }


}