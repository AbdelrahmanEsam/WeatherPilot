package com.example.weatherpilot

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherpilot.data.local.datastore.DataStoreUserPreferences
import com.example.weatherpilot.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding


    @Inject
    lateinit var dataStore: DataStoreUserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
        currentFragmentObserver()
        langToRtlObserver()

    }


    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)
    }


    private fun currentFragmentObserver() {
        navHostFragment.navController.addOnDestinationChangedListener { navController: NavController?, navDestination: NavDestination?, bundle: Bundle? ->
            val currentDestination =
                navHostFragment.navController.currentDestination!!.id
            if (currentDestination == R.id.splashFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }

        }
    }

    private fun langToRtlObserver() {

        lifecycleScope.launch(Dispatchers.IO) {
            dataStore.getString("languageType")
                .catch { Log.d("error", it.message.toString()) }
                .collect {
                    it?.let {
                        withContext(Dispatchers.Main) {
                            if (it == getString(R.string.ar)) {
                                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                                updateResources(getString(R.string.ar))
                            } else {
                                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                updateResources(getString(R.string.en))
                            }

                        }
                    }
                }
        }




    }


    private fun updateResources(language: String){
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}