package com.example.weatherpilot.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentHomeBinding
import com.example.weatherpilot.domain.model.DayWeatherModel
import com.example.weatherpilot.domain.model.HourWeatherModel
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.util.connectivity.ConnectivityObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class HomeFragment(
    private val locationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val locationManager: LocationManager,
    private val connectivityObserver: ConnectivityObserver,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher,
) : Fragment() {


    private lateinit var binding: FragmentHomeBinding


    private lateinit var navController: NavController


    private val viewModel: HomeViewModel by viewModels()


    private val hoursAdapter by lazy {
        HoursRecyclerAdapter()
    }
    private val daysAdapter by lazy {
        DaysRecyclerAdapter()
    }

    private lateinit var locationCallback: LocationCallback




    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        permissions.forEach { permission ->
            if (!permission.value) {
                binding.grantLocationPermissionDialog.visibility = View.VISIBLE
                binding.descriptionTextView.text = getString(R.string.give_location_permission)
                binding.grantButton.text = getString(R.string.grant)
                return@registerForActivityResult
            }
        }
    getLastLocationFromGPS()

    }


    private val startLocationActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (!isLocationEnabled()) {
                binding.grantLocationPermissionDialog.visibility = View.VISIBLE
                binding.descriptionTextView.text = getString(R.string.please_enable_location)
                binding.grantButton.text = getString(R.string.enable)
                return@registerForActivityResult
            }
           getLastLocationFromGPS()
        }


    private val startPermissionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (!checkPermission()) {
                binding.grantLocationPermissionDialog.visibility = View.VISIBLE
                binding.descriptionTextView.text = getString(R.string.please_enable_location)
                binding.grantButton.text = getString(R.string.enable)
                return@registerForActivityResult
            }
            binding.refreshLayout.isRefreshing = true
            getLastLocationFromGPS()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        getFavouriteLocation()
        setHoursRecyclerView()
        setDaysRecyclerView()
        gpsLocationCallback()
        displayStateObserver()
        latLongStateObserver()
        connectivityObserver()

        binding.refreshLayout.setOnRefreshListener {
            loadingAndFetchData()
        }

        binding.grantButton.setOnClickListener {
            if (binding.grantButton.text == getString(R.string.grant)) {
                openPermissionsPage()
            } else {
                startLocationPage()
            }

        }
    }

    private fun openPermissionsPage() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startPermissionActivityForResult.launch(intent)
    }


    private fun getFavouriteLocation(){
        val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(getString(R.string.locationType), Location::class.java)
        } else {
            arguments?.getParcelable(getString(R.string.locationType))
        }



        location?.let {
            viewModel.onEvent(
                HomeIntent.FetchDataOfFavouriteLocation(
                    it.longitude,
                    it.latitude
                )
            )

        } ?: kotlin.run {
            viewModel.onEvent(HomeIntent.ReadPrefsFromDataStore)
        }
    }


    private fun loadingAndFetchData() {
        binding.refreshLayout.isRefreshing = true
        if (viewModel.statePreferences.value.locationType.equals(getString(R.string.gps_type))) {
            getLastLocationFromGPS()
        }
        viewModel.onEvent(HomeIntent.FetchData)

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


        private fun latLongStateObserver() {
        lifecycleScope.launch {
            viewModel.statePreferences.collectLatest {
                    if (viewModel.statePreferences.value.locationType.equals(getString(R.string.gps_type))) {

                        getLastLocationFromGPS()
                    } else if (viewModel.statePreferences.value.locationType.equals(getString(R.string.map_type))) {
                        viewModel.onEvent(HomeIntent.ReadLatLongFromDataStore)
                    }else{
                        viewModel.onEvent(HomeIntent.FetchData)
                    }
            }
        }
    }






    private fun displayStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.stateDisplay.collectLatest { state ->
                    if (state.dayState.isNotEmpty()) {
                        setHourlyDataToRecyclerView(state.dayState)
                    }

                    if (state.dayState.isNotEmpty()) {
                        setDaysDataToRecyclerView(state.weekState)
                    }



                    state.loading?.let { loading ->

                        binding.refreshLayout.isRefreshing = loading
                        if (state.dayState.isNotEmpty()) {
                            binding.contentLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }


    private fun connectivityObserver() {
        lifecycleScope.launch(ioDispatcher) {
            connectivityObserver.observe().collectLatest { status ->
                withContext(mainDispatcher) {
                    if (status == ConnectivityObserver.Status.Lost || status == ConnectivityObserver.Status.Unavailable) {
                        binding.refreshLayout.isRefreshing = true

                    }


                    if (status == ConnectivityObserver.Status.Available && checkPermission()) {
                        loadingAndFetchData()
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


    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    private fun checkPermission(): Boolean {
        if (
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            return true
        }
        return false
    }

    private fun getLastLocationFromGPS() {
        if (!checkPermission()) {

            requestPermission(); return
        }
        if (!isLocationEnabled()) {

            startLocationPage();return
        }
        locationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private fun startLocationPage() {
        Toast.makeText(
            requireContext(),
            getString(R.string.turn_on_location_please),
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startLocationActivityForResult.launch(intent)
    }


    private fun gpsLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (viewModel.statePreferences.value.locationType.equals(getString(R.string.gps_type))) {
                    val location = locationResult.lastLocation
                    viewModel.onEvent(
                        HomeIntent.NewLocationFromGPS(
                            location?.longitude.toString(),
                            location?.latitude.toString()
                        )
                    )
                }
            }
        }
    }


    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }








}