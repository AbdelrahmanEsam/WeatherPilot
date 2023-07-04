package com.example.weatherpilot.presentation.map

import android.app.NotificationManager
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentMapBinding
import com.example.weatherpilot.util.usescases.getAddress
import com.example.weatherpilot.util.coroutines.searchFlow
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class MapFragment(
    private val ioDispatcher: CoroutineDispatcher,
    private val notificationManager: NotificationManager
) : Fragment() {


    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private var previousDestination: String? = null

    private val englishGeoCoder by lazy { Geocoder(requireContext(), Locale.US) }
    private val arabicGeoCoder by lazy {
        Geocoder(
            requireContext(),
            Locale(getString(R.string.ar))
        )
    }

    private val viewModel: MapViewModel by viewModels()


    private val searchAdapter by lazy {
        SearchAdapter {
            onSearchItemClickListener(it)
        }
    }

    private val supportMapFragment: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
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
        alertStateObserver()
        searchRecyclerSetup()
        googleMapHandler()
        searchViewActions()
        backPressedHandler()

        binding.go.setOnClickListener {
            when (previousDestination) {
                getString(R.string.from_favourite_fragment) -> {
                    viewModel.onEvent(MapIntent.SaveFavourite)
                }

                getString(R.string.from_alerts_fragment) -> {
                    if (viewModel.alertState.value.longitude.isBlank()) {
                        viewModel.onEvent(MapIntent.ShowSnackBar(getString(R.string.please_choose_place_on_the_map)))
                        return@setOnClickListener
                    }
                    navController.navigate(NavGraphDirections.actionToDatePicker())

                }

                else -> {
                    viewModel.onEvent(MapIntent.SaveDataToDataStore)
                }
            }
        }


    }

    private fun datePickerFlowObserver() {

        lifecycleScope.launch {
            navController
                .currentBackStackEntry?.savedStateHandle?.getStateFlow(
                    getString(R.string.date),
                    ""
                )?.collectLatest { date ->
                    if (date.isNotEmpty()) {
                        viewModel.onEvent(MapIntent.SetAlarmDateIntent(date))
                        Handler(Looper.getMainLooper()).postDelayed({
                            Log.d("datePicker", date)
                            navController.navigate(NavGraphDirections.actionToTimePicker())
                        }, 500)
                    }
                }
        }

    }


    private fun timePickerFlowObserver() {

        lifecycleScope.launch {
            navController
                .currentBackStackEntry?.savedStateHandle?.getStateFlow(
                    getString(R.string.time),
                    ""
                )?.collectLatest { time ->
                    if (time.isNotEmpty()) {
                        viewModel.onEvent(MapIntent.SetAlarmTimeIntent(time))
                        viewModel.onEvent(MapIntent.SaveAlert)

                    }
                }
        }

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

    private fun onSearchItemClickListener(address: Address) {
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchInputLayout.editText?.clearFocus()
        closeKeyboard()
        supportMapFragment.getMapAsync { googleMap ->
            with(address) {
                val latLng = LatLng(latitude, longitude)
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

                binding.progressBar.visibility = View.VISIBLE
                if (resources.getBoolean(R.bool.is_english)) {
                    englishGeoCoder.getAddress(it) { searchResultFlow ->
                        collectSearchResultFlow(searchResultFlow)

                    }
                } else {
                    arabicGeoCoder.getAddress(it) { searchResultFlow ->
                        collectSearchResultFlow(searchResultFlow)
                    }
                }

            }
        }
    }


    private fun collectSearchResultFlow(searchFlow: Flow<List<Address?>>) {
        lifecycleScope.launch {
            searchFlow.collectLatest {
                binding.progressBar.visibility = View.GONE
                searchAdapter.submitList(it)
            }
        }
    }

    private fun decideStateObserver() {


        when (previousDestination) {
            getString(R.string.from_favourite_fragment) -> {
                alertStateObserver()

            }

            getString(R.string.from_alerts_fragment) -> {
                favouriteStateObserver()
                datePickerFlowObserver()
                timePickerFlowObserver()
            }

            else -> {
                regularStateObserver()
            }
        }
        snackBarObserver()
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
        when (previousDestination) {
            getString(R.string.from_favourite_fragment) -> {

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
            }

            getString(R.string.from_alerts_fragment) -> {

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

            }

            else -> {
                setMarkerAndAnimateCamera(latLong, googleMap)
                viewModel.onEvent(
                    MapIntent.NewLatLong(
                        latLong.latitude.toString(),
                        latLong.longitude.toString()
                    )
                )
            }
        }
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
                .zoom(15f)
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