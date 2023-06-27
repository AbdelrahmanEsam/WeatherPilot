package com.example.weatherpilot.presentation.map

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

@AndroidEntryPoint
class MapFragment : Fragment() {


    private lateinit var binding : FragmentMapBinding
    private lateinit var navController : NavController

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
        googleMapHandler()


        binding.go.setOnClickListener {

            viewModel.onEvent(MapIntent.SaveDataToDataStore)
            navController.popBackStack()
        }
        stateObserver()
    }

    private fun stateObserver()
    {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect{
                    when(it.saveState){
                        true -> {navController.popBackStack()}
                        false -> binding.progressBar.visibility = View.VISIBLE
                        null -> {}
                    }

                    when(it.mapLoadingState){
                        true -> binding.progressBar.visibility = View.VISIBLE
                        false -> binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun googleMapHandler()
    {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->
            googleMap.setOnMapLongClickListener {
                googleMap.addMarker( MarkerOptions()
                    .position(it))
                viewModel.onEvent(MapIntent.NewLatLong(it.latitude.toString(),it.longitude.toString()))
            }

            googleMap.setOnMapLoadedCallback {
                viewModel.onEvent(MapIntent.MapLoaded)
            }

        }


    }



}