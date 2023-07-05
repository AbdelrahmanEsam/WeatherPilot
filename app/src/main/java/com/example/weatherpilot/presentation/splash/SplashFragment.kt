package com.example.weatherpilot.presentation.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class SplashFragment(
    private val dataStore: DataStoreUserPreferences,
    private val ioDispatcher: CoroutineDispatcher
) : Fragment() {

    private lateinit var binding: FragmentSplashBinding


    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController(view)
//        binding.motionLayout.transitionToStart()

        binding.lottieSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                readPrefFromDataStore(getString(R.string.locationType),dataStore) { result ->
                    when (result) {
                        null -> {
                            navController.navigate(SplashFragmentDirections.actionSplashFragmentToFirstTimeFragmentDialog())

                        }
                        getString(R.string.gps_type) -> {
                            navController.navigate(
                                SplashFragmentDirections.actionSplashFragmentToHomeFragment(
                                    null
                                )
                            )
                        }
                        getString(R.string.map_type) -> {
                            readPrefFromDataStore(getString(R.string.latitudeType),dataStore){  latitude ->
                                latitude?.let {
                                    navController.navigate( SplashFragmentDirections.actionSplashFragmentToHomeFragment(
                                        null
                                    ))
                                } ?: run {
                                    navController.navigate(NavGraphDirections.actionToMapFragment(null))
                                }
                            }

                        }
                    }
                }
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })


    }


    private fun readPrefFromDataStore(
        key : String,
        dataStore: DataStoreUserPreferences,
        dataStoreResult: (String?) -> Unit
    ) {
        lifecycleScope.launch(ioDispatcher) {
            val locationTypeResult = dataStore.getString(key).first()

            withContext(Dispatchers.Main) {
                dataStoreResult(locationTypeResult)
            }
        }
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}