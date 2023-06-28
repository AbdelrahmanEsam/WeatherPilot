package com.example.weatherpilot.presentation.map

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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MapFragment : Fragment() {


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



            } else {
                viewModel.onEvent(MapIntent.SaveDataToDataStore)
                navController.popBackStack()
            }
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
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLong)
                )



                if (previousDestination == getString(R.string.from_favourite_fragment)) {
                    val(englishCityName , arabicCityName) = getCityNameByLatLong(latLong.latitude,latLong.longitude)
                    arabicCityName?.let {
                        viewModel.onEvent(MapIntent.SaveLocationToFavourites(
                            arabicName =  arabicCityName , englishName =  englishCityName!!
                            , longitude = latLong.longitude.toString(), latitude = latLong.latitude.toString() ))
                    }
                }else
                {
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
            }

        }


    }

    private fun getCityNameByLatLong(lat: Double, long: Double) : Pair<String? ,String?>{
        val englishGeoCoder = Geocoder(requireContext(), Locale.US)
        val arabicGeoCoder = Geocoder(requireContext(), Locale("ar"))
        var englishCityName : String? = null
        var arabicCityName : String? = null
         englishGeoCoder.getAddress(lat, long){
            englishCityName = it?.getAddressLine(0)
        }
        arabicGeoCoder.getAddress(lat, long){
            arabicCityName = it?.getAddressLine(0)
        }
        return Pair(englishCityName,arabicCityName)
    }


    @Suppress("DEPRECATION")
    fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (android.location.Address?) -> Unit
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
            return
        }

        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch(e: Exception) {
            address(null)
        }
    }


}