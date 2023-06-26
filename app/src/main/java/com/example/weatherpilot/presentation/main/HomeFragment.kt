package com.example.weatherpilot.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentHomeBinding
import com.example.weatherpilot.domain.model.DayWeatherModel
import com.example.weatherpilot.domain.model.HourWeatherModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment(private val locationClient: FusedLocationProviderClient, private val locationRequest: LocationRequest) : Fragment() {


    private lateinit var binding: FragmentHomeBinding


    private lateinit var navController: NavController


    private val viewModel: HomeViewModel by viewModels()


    private val hoursAdapter by lazy {
        HoursRecyclerAdapter { itemPosition ->

        }
    }
 private val daysAdapter by lazy {
        DaysRecyclerAdapter { itemPosition ->

        }
    }

    private lateinit var  locationCallback: LocationCallback






     private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { permission ->
            if (!permission.value) {
                Toast.makeText(requireContext(), "${permission.key} is needed", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
        }
         getLastLocationFromGPS()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setHoursRecyclerView()
        setDaysRecyclerView()
        callback()
        displayStateObserver()
        latLongStateObserver()
    }


    private fun setHoursRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.hourRecyclerView.layoutManager = linearLayoutManager
        binding.hourRecyclerView.adapter = hoursAdapter
    }


    private fun setDaysRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.daysRecyclerView.layoutManager = linearLayoutManager
        binding.daysRecyclerView.adapter = daysAdapter
    }


    private fun displayStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.stateDisplay.collect { state ->
                    if (state.dayState.isNotEmpty()) {
                        setHourlyDataToRecyclerView(state.dayState)
                    }

                    if (state.dayState.isNotEmpty()) {
                        setDaysDataToRecyclerView(state.weekState)
                    }
                }
            }
        }
    }


    private fun latLongStateObserver()
    {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.statePreferences.collect {

                    if (viewModel.statePreferences.value.locationType.equals(getString(R.string.gps)))
                    {
                       getLastLocationFromGPS()
                    }else if (viewModel.statePreferences.value.locationType.equals(getString(R.string.map))){
                        viewModel.onEvent(HomeIntent.ReadLatLongFromDataStore)
                    }

                }
            }
        }
    }

    private fun setHourlyDataToRecyclerView(list: List<HourWeatherModel>) {
        hoursAdapter.submitList(list)
    }


    private fun setDaysDataToRecyclerView(list: List<DayWeatherModel>) {
        daysAdapter.submitList(list)
    }



    private fun requestPermission(){
        requestPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun checkPermission(): Boolean{
        if (
            (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ){
            return true
        }
        return false
    }

    private fun getLastLocationFromGPS() {

        if (!checkPermission())  {requestPermission() ; return}
        if (!isLocationEnabled()) {
            startLocationPage();return}

        locationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private fun startLocationPage()
    {
        Toast.makeText(requireContext(), "Turn on location please", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    private fun callback()
    {
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                Log.d("location callback",location.toString())
                viewModel.onEvent(HomeIntent.NewLocationFromGPS(
                    location?.longitude.toString(),
                    location?.latitude.toString()))
            }
        }
    }




    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }




}