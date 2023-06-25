package com.example.weatherpilot.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherpilot.domain.usecase.GetCurrentTimeStampUseCase
import com.example.weatherpilot.domain.usecase.GetWeatherDataUseCase
import com.example.weatherpilot.util.Dispatcher
import com.example.weatherpilot.util.Dispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.roundToInt


@HiltViewModel
class HomeViewModel @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getCurrentTimeStampUseCase: GetCurrentTimeStampUseCase,
) : ViewModel() {


    private val _state : MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
     val state = _state.asStateFlow()


    fun onEvent(intent: HomeIntent)
    {
        when(intent){
            is HomeIntent.NewLocation ->getWeatherResponse(intent.longitude,intent.latitude)
        }

    }

    private fun getWeatherResponse(longitude : String , latitude : String)
    {

        viewModelScope.launch(ioDispatcher) {

            try {


             val weatherResponse = getWeatherDataUseCase.execute(longitude = longitude, latitude =  latitude)


                with(weatherResponse){ _state.update {it.copy(city = city, weatherState =   description
                    , pressure =  pressure.toString()
                    , clouds = clouds.toString()
                    , humidity = humidity.toString()
                    , wind = wind.toString()
                    , dayState = hoursWeather
                    , temp = temp.roundToInt().toString()
                    , visibility = visibility.toString()
                    , iconCode = icon
                    , weekState = daysWeather ?: listOf()
                )
                }}



            }catch (e : Exception)
            {
             Log.d("weatherError",e.message.toString())
            }


         Log.d("weatherState",state.value.toString())

        }
    }



    init {

        getWeatherResponse("10.99","10.34")
    }


}