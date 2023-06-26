package com.example.weatherpilot.presentation.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentHomeBinding
import com.example.weatherpilot.databinding.FragmentSettingsBinding
import com.example.weatherpilot.presentation.map.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingsFragment : Fragment() {


    private lateinit var binding: FragmentSettingsBinding


    private lateinit var navController: NavController


    private val viewModel: SettingsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        stateObserver()

        binding.GPSRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.LocationChange(getString(R.string.gps)))
        }

        binding.mapRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.LocationChange(getString(R.string.map)))
        }

        binding.arabicRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.LanguageChange(getString(R.string.arabic)))
        }

        binding.englishRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.LanguageChange(getString(R.string.english)))
        }


        binding.meterRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.WindChange(getString(R.string.meter_sec)))
        }


        binding.mileRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.WindChange(getString(R.string.mile_hour)))
        }


        binding.celsiusRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.TemperatureChange(getString(R.string.celsius)))
        }

        binding.kelvinRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.TemperatureChange(getString(R.string.kelvin)))
        }

        binding.fahrenheitRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.TemperatureChange(getString(R.string.fahrenheit)))
        }

        binding.enabledRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.NotificationChange(getString(R.string.enabled)))
        }

        binding.disabledRadioImageView.setOnClickListener {
            viewModel.onEvent(SettingsIntent.NotificationChange(getString(R.string.disabled)))
        }

    }


    private fun stateObserver()
    {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect{
                    when(it.locationType){

                        getString(R.string.gps) -> {
                            Log.d("locationCollected", it.locationType.toString())
                            binding.GPSRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.mapRadioImageView.setImageResource(R.drawable.radio_unchecked)

                        }
                        getString(R.string.map) -> {
                            Log.d("locationCollected", it.locationType.toString())
                            binding.GPSRadioImageView.setImageResource(R.drawable.radio_unchecked)
                            binding.mapRadioImageView.setImageResource(R.drawable.radio_checked)
                        }


                    }

                    when(it.languageType){
                        getString(R.string.arabic) -> {
                            binding.arabicRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.englishRadioImageView.setImageResource(R.drawable.radio_unchecked)

                        }
                        getString(R.string.english) -> {
                            binding.englishRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.arabicRadioImageView.setImageResource(R.drawable.radio_unchecked)
                        }
                    }


                    when(it.windType){
                        getString(R.string.meter_sec) -> {
                            binding.meterRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.mileRadioImageView.setImageResource(R.drawable.radio_unchecked)

                        }
                        getString(R.string.mile_hour) -> {
                            binding.mileRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.meterRadioImageView.setImageResource(R.drawable.radio_unchecked)
                        }
                    }


                    when(it.temperatureType){
                        getString(R.string.celsius) -> {
                            Log.d("temp", it.temperatureType.toString())
                        binding.celsiusRadioImageView.setImageResource(R.drawable.radio_checked)
                        binding.kelvinRadioImageView.setImageResource(R.drawable.radio_unchecked)
                        binding.fahrenheitRadioImageView.setImageResource(R.drawable.radio_unchecked)

                    }
                        getString(R.string.kelvin) -> {
                            binding.celsiusRadioImageView.setImageResource(R.drawable.radio_unchecked)
                            binding.kelvinRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.fahrenheitRadioImageView.setImageResource(R.drawable.radio_unchecked)
                    }

                        getString(R.string.fahrenheit) -> {
                            binding.celsiusRadioImageView.setImageResource(R.drawable.radio_unchecked)
                            binding.kelvinRadioImageView.setImageResource(R.drawable.radio_unchecked)
                            binding.fahrenheitRadioImageView.setImageResource(R.drawable.radio_checked)
                        }

                    }

                    when(it.notificationType){
                        getString(R.string.enabled) -> {
                            binding.enabledRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.disabledRadioImageView.setImageResource(R.drawable.radio_unchecked)

                        }
                        getString(R.string.disabled) -> {
                            binding.disabledRadioImageView.setImageResource(R.drawable.radio_checked)
                            binding.enabledRadioImageView.setImageResource(R.drawable.radio_unchecked)
                        }
                    }
                }
            }
        }
    }
}