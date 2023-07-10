package com.example.weatherpilot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
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
import com.example.weatherpilot.util.connectivity.ConnectivityObserver
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers.*
import com.example.weatherpilot.util.usescases.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
     lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    @Inject
    @Dispatcher(IO)lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var  notificationManager: NotificationManager



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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

    }


    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)
    }


    private fun currentFragmentObserver() {
        navHostFragment.navController.addOnDestinationChangedListener { _: NavController?, _: NavDestination?, _: Bundle? ->
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
        lifecycleScope.launch(ioDispatcher) {
            dataStore.getString<String>(getString(R.string.languagetype))
                .catch { Log.d("error", it.message.toString()) }
                .collect {languageTypeResult->
                    languageTypeResult.let {
                        withContext(Dispatchers.Main) {

                            if (languageTypeResult is Response.Success) {

                                if (languageTypeResult.data == getString(R.string.ar)) {
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


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        Log.d("channel","channel is created")
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel( getString(R.string.weather_alert), getString(R.string.weather_alert), importance)
        channel.description = getString(R.string.weather_alert)
        notificationManager.createNotificationChannel(channel)
    }




    private fun updateResources(language: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }


    private fun enableInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }


    private fun disableInteraction() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
    }
}