package com.example.weatherpilot.presentation.map

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentMapBinding
import com.example.weatherpilot.domain.model.SearchItem
import com.example.weatherpilot.util.connectivity.ConnectivityObserver
import com.example.weatherpilot.util.coroutines.searchFlow
import com.example.weatherpilot.util.usescases.getAddress
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@AndroidEntryPoint
class MapFragment(
    private val ioDispatcher: CoroutineDispatcher,
    private val englishGeoCoder: Geocoder,
    private val arabicGeoCoder: Geocoder,
    private val connectivityObserver: ConnectivityObserver
) : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private var previousDestination: String? = null


    private val viewModel: MapViewModel by viewModels()


    private val calender by lazy { Calendar.getInstance() }


    private val searchAdapter by lazy {
        SearchAdapter {
            onSearchItemClickListener(it)
        }
    }

    private val supportMapFragment: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
    }


    private val datePicker by lazy {
        MaterialDatePicker.Builder.datePicker().setTheme(R.style.datePickerTheme)
            .setCalendarConstraints(
                CalendarConstraints.Builder().setStart(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            ).build()
    }

    private val timePicker by lazy {
        MaterialTimePicker.Builder().setHour(
            calender.get(Calendar.HOUR_OF_DAY),
        ).setMinute(calender.get(Calendar.MINUTE)).setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTheme(R.style.timePickerTheme).build()
    }


    private val startDrawingPermissionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (!Settings.canDrawOverlays(requireContext())) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.give_drawing_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private val startNotificationPermissionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (!Settings.canDrawOverlays(requireContext())) {
                val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                settingsIntent.apply {

                    data = Uri.parse("package:" + requireContext().packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {

                Toast.makeText(
                    requireContext(),
                    getString(R.string.give_notification_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    private val chooseKindAlert by lazy {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_alert_type)
            .setPositiveButton(R.string.notification) { dialog, _ ->
                if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    settingsIntent.apply {
                        data = Uri.parse("package:" + requireContext().packageName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startNotificationPermissionActivityForResult.launch(settingsIntent)
                } else {
                    viewModel.onEvent(MapIntent.SetAlarmType(getString(R.string.notificationtype)))
                    dialog.dismiss()
                    datePicker.show(parentFragmentManager, "")
                }
            }.setNegativeButton(R.string.alerts) { dialog, _ ->
                if (!Settings.canDrawOverlays(requireContext())) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    settingsIntent.apply {

                        data = Uri.parse("package:" + requireContext().packageName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startDrawingPermissionActivityForResult.launch(settingsIntent)
                } else {
                    viewModel.onEvent(MapIntent.SetAlarmType(getString(R.string.alertType)))
                    dialog.dismiss()
                    datePicker.show(parentFragmentManager, "")
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        previousDestination = arguments?.getString(getString(R.string.previous))
        decideStateObserver()
        searchRecyclerSetup()
        searchResultStateObserver()
        googleMapHandler()
        searchViewActions()
        backPressedHandler()
        connectivityObserver()
        binding.go.setOnClickListener {
            stateDecider(favouriteImpl = {
                viewModel.onEvent(MapIntent.SaveFavourite)
            }, {
                if (viewModel.alertState.value.longitude.isBlank()) {
                    viewModel.onEvent(MapIntent.ShowSnackBar(R.string.please_choose_place_on_the_map))
                    return@stateDecider
                }
                chooseKindAlert.show()
            }, regularImpl = {
                viewModel.onEvent(MapIntent.SaveLocationToDataStore)
            }, {})
        }



        datePicker.addOnPositiveButtonClickListener {
            calender.timeInMillis = it
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH).plus(1)
            val day = calender.get(Calendar.DAY_OF_MONTH)
            viewModel.onEvent(MapIntent.SetAlarmDateIntent("$year $month $day"))
            timePicker.show(parentFragmentManager, "")
        }



        timePicker.addOnPositiveButtonClickListener {

            with(timePicker) {
                viewModel.onEvent(MapIntent.SetAlarmTimeIntent("$hour $minute"))
                viewModel.onEvent(MapIntent.SaveAlert)
            }
        }


    }


    private fun stateDecider(
        favouriteImpl: () -> Unit,
        alertImpl: () -> Unit,
        regularImpl: () -> Unit,
        optionalImpl: () -> Unit
    ) {
        when (previousDestination) {
            getString(R.string.from_favourite_fragment) -> {
                favouriteImpl.invoke()
            }

            getString(R.string.from_alerts_fragment) -> {
                alertImpl.invoke()
            }

            else -> {
                regularImpl.invoke()
            }
        }


        optionalImpl.invoke()

    }


    private fun backPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.searchRecyclerView.visibility == View.VISIBLE) {
                        binding.searchRecyclerView.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        binding.searchInputLayout.editText?.clearFocus()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }


    private fun searchRecyclerSetup() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.searchRecyclerView.layoutManager = linearLayoutManager
        binding.searchRecyclerView.adapter = searchAdapter
    }

    private fun onSearchItemClickListener(searchItem: SearchItem) {
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchInputLayout.editText?.clearFocus()
        closeKeyboard()
        supportMapFragment.getMapAsync { googleMap ->
            with(searchItem) {
                val latLng = LatLng(lat, lon)
                newMapLocationIsSelected(latLng, googleMap)
            }
        }
    }

    private fun closeKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun searchViewActions() {
        binding.searchInputLayout.editText?.setOnFocusChangeListener { _, focused ->
            if (focused) binding.searchRecyclerView.visibility = View.VISIBLE
        }

        binding.searchInputLayout.editText?.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_GO) {
                binding.searchRecyclerView.visibility = View.GONE
            }
            true
        }

        lifecycleScope.launch {
            binding.searchInputLayout.searchFlow().collectLatest {
                if (it.isNotEmpty()) {

                    viewModel.onEvent(MapIntent.SearchCityName(it))
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    viewModel.onEvent(MapIntent.ClearSearchList)
                }
            }
        }
    }


    private fun decideStateObserver() {
        stateDecider(favouriteImpl = {
            favouriteStateObserver()
        }, alertImpl = {
            alertStateObserver()
        }, regularImpl = { regularStateObserver() }) {
            snackBarObserver()
        }
    }

    private fun regularStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collectLatest {
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
                            navController.navigate(NavGraphDirections.actionToFavourites())
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


    private fun alertStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.alertState.collect {
                    when (it.saveState) {
                        true -> {
                            navController.navigate(NavGraphDirections.actionToNotificationFragment())
                        }

                        false -> binding.progressBar.visibility = View.VISIBLE
                        null -> {}
                    }

                    when (it.mapLoadingState) {
                        true -> binding.progressBar.visibility = View.VISIBLE
                        false -> binding.progressBar.visibility = View.GONE
                    }

                    if (it.insertNotification == true) navController.popBackStack()
                }
            }
        }
    }


    private fun searchResultStateObserver() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.searchResultState.collect {

                    binding.progressBar.visibility = View.GONE
                    searchAdapter.submitList(it.searchResult?.searchResults)

                }
            }
        }
    }


    private fun snackBarObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.snackBarFlow.collectLatest { errorMessage ->
                    Snackbar.make(binding.root, getString(errorMessage), Snackbar.LENGTH_LONG)
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


    private fun connectivityObserver() {
        lifecycleScope.launch(ioDispatcher) {
            connectivityObserver.observe().collectLatest { status ->
                withContext(Dispatchers.Main) {
                    if (status == ConnectivityObserver.Status.Lost) {
                        navController.apply {
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()
                            navigate(NavGraphDirections.actionToHomeFragment(null), navOptions)

                        }

                    }
                }
            }
        }
    }


    private fun googleMapHandler() {

        supportMapFragment.getMapAsync { googleMap ->


            googleMap.setOnMapLongClickListener { latLong ->

                newMapLocationIsSelected(latLong, googleMap)

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
                            setMarkerAndAnimateCamera(
                                LatLng(latitude.toDouble(), longitude!!.toDouble()),
                                googleMap
                            )
                        }
                    }
                }
            }

        }


    }

    private fun newMapLocationIsSelected(latLong: LatLng, googleMap: GoogleMap) {


        stateDecider(favouriteImpl = {
            viewModel.onEvent(MapIntent.NewFavouriteLocation("", "", "", ""))
            getCityNameByLatLong(latLong, googleMap) { englishAddress, arabicAddress ->
                viewModel.onEvent(
                    MapIntent.NewFavouriteLocation(
                        if (!arabicAddress.locality.isNullOrBlank()) arabicAddress.locality else arabicAddress.countryName,
                        if (!englishAddress.locality.isNullOrBlank()) englishAddress.locality else englishAddress.countryName,
                        latLong.latitude.toString(),
                        latLong.longitude.toString()
                    )
                )
            }
        }, alertImpl = {

            viewModel.onEvent(MapIntent.AlertLocationIntent("", "", "", ""))
            getCityNameByLatLong(latLong, googleMap) { englishAddress, arabicAddress ->
                viewModel.onEvent(
                    MapIntent.AlertLocationIntent(
                        if (!arabicAddress.locality.isNullOrBlank()) arabicAddress.locality else arabicAddress.countryName,
                        if (!englishAddress.locality.isNullOrBlank()) englishAddress.locality else englishAddress.countryName,
                        latLong.latitude.toString(),
                        latLong.longitude.toString()
                    )
                )
            }


        }, regularImpl = {

            setMarkerAndAnimateCamera(latLong, googleMap)
            viewModel.onEvent(
                MapIntent.NewLatLong(
                    latLong.latitude.toString(),
                    latLong.longitude.toString()
                )
            )

        }, {})

    }


    private fun setMarkerAndAnimateCamera(latLong: LatLng, googleMap: GoogleMap) {
        googleMap.apply {
            clear()
            addMarker(
                MarkerOptions()
                    .position(latLong)
            )

            val cameraPosition = CameraPosition.Builder()
                .target(latLong)
                .zoom(5f)
                .build()
            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                1500,
                null
            )
        }

    }

    private fun getCityNameByLatLong(
        latLong: LatLng,
        googleMap: GoogleMap,
        onEvent: (Address, Address) -> Unit
    ) {


        englishGeoCoder.getAddress(latLong.latitude, latLong.longitude) { englishFlow ->
            arabicGeoCoder.getAddress(
                latLong.latitude,
                latLong.longitude
            ) { arabicFlow ->
                lifecycleScope.launch(ioDispatcher) {
                    englishFlow.combine(arabicFlow) { englishAddress, arabicAddress ->


                        if (englishAddress == null) return@combine
                        if (arabicAddress == null) return@combine


                        onEvent.invoke(englishAddress, arabicAddress)



                        withContext(Dispatchers.Main) {
                            setMarkerAndAnimateCamera(latLong, googleMap)
                        }

                    }.collect()
                }
            }

        }


    }


}