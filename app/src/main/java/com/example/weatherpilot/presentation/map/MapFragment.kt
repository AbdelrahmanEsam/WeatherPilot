package com.example.weatherpilot.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentMapBinding
import com.google.android.gms.maps.SupportMapFragment

class MapFragment : Fragment() {


    private lateinit var binding : FragmentMapBinding
    private lateinit var navController : NavController
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
    }


    private fun googleMapHandler()
    {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->

            googleMap.setOnMapClickListener {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set(getString(R.string.longitude),it.longitude.toString())
                navController.previousBackStackEntry?.savedStateHandle
                ?.set(getString(R.string.latitude), it.latitude.toString())
            }
        }
    }
}