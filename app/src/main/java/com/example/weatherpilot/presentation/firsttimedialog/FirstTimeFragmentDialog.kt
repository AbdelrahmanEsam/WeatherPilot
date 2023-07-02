package com.example.weatherpilot.presentation.firsttimedialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherpilot.MainActivity
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentFirstTimeDialogBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FirstTimeFragmentDialog : DialogFragment() {

    lateinit var binding: FragmentFirstTimeDialogBinding

    private lateinit var navController: NavController


    private val viewModel: FirstTimeViewModel by viewModels()

    override fun getTheme() = R.style.RoundedCornersDialog




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstTimeDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        stateCollector()
        snackBarStateCollector()
        binding.mapRadioImageView.setOnClickListener {
            viewModel.onEvent(FirstTimeIntent.PlaceStateChanged(getString(R.string.map_type)))
        }

        binding.GPSRadioImageView.setOnClickListener {
            viewModel.onEvent(FirstTimeIntent.PlaceStateChanged(getString(R.string.gps_type)))
        }

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onEvent(FirstTimeIntent.NotificationStateChanged(getString(R.string.enabled_type)))
            } else {
                viewModel.onEvent(FirstTimeIntent.NotificationStateChanged(getString(R.string.disabled_type)))
            }
        }
        binding.goButton.setOnClickListener {
            viewModel.onEvent(FirstTimeIntent.Go)
            navigator()
        }
    }


    override fun onResume() {
        super.onResume()
        setFullScreen()
    }

    private fun navigator()
    {
        if (viewModel.state.value.locationType == getString(R.string.gps_type))
        {
            navController.navigate(NavGraphDirections.actionToHomeFragment(null))

        }else{
            navController.navigate(NavGraphDirections.actionToMapFragment(null))
        }
    }


    private fun stateCollector() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                if (state.locationType == getString(R.string.map_type)) {
                    binding.GPSRadioImageView.setImageResource(R.drawable.radio_unchecked)
                    binding.mapRadioImageView.setImageResource(R.drawable.radio_checked)
                } else {
                    binding.GPSRadioImageView.setImageResource(R.drawable.radio_checked)
                    binding.mapRadioImageView.setImageResource(R.drawable.radio_unchecked)
                }




                binding.notificationSwitch.isEnabled =
                    state.notificationType == getString(R.string.enabled_type)
            }
        }
    }

    private fun snackBarStateCollector() {
        lifecycleScope.launch {
            viewModel.snackBarState.collectLatest { message ->
                Snackbar.make((requireActivity() as MainActivity).binding.root, message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.baby_blue))
                    .show()
            }
        }
    }

    private fun DialogFragment.setFullScreen() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}