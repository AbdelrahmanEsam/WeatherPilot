package com.example.weatherpilot.util.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.Location
import java.util.Locale


object TextAdapters{

@JvmStatic
@BindingAdapter("windWithType")
fun windWithType(textView: TextView, wind : String?) {
    wind?.let {
        val result =  when(wind.split(" ")[1]){
        textView.context.getString(R.string.meter_sec_type) -> wind.split(" ")[0] +" "+textView.context.getString(R.string.meter_sec)

        else -> wind.split(" ")[0]+ " "+textView.context.getString(R.string.mile_hour)
        }
        textView.text = result
    }
}


    @JvmStatic
    @BindingAdapter("tempWithType")
    fun tempWithType(textView: TextView, temp : String?) {
        temp?.let {
            with(textView.context){

            val result =  when(temp.split(" ")[1]){

                getString(R.string.fahrenheit_type) -> temp.split(" ")[0] +" "+getString(R.string.fahrenheit_symbol)

                getString(R.string.kelvin_type) -> temp.split(" ")[0]+ " "+getString(R.string.kelvin_symbol)


                else -> temp.split(" ")[0]+ " "+getString(R.string.celsius_symbol)
            }
            textView.text = result
            }
        }
    }


    @JvmStatic
    @BindingAdapter("setArabicOrEnglish")
    fun setArabicOrEnglish(textView: TextView, location : Location?) {

      val locale = Locale.getDefault()
        if (locale.language == "en") {
            textView.text = location?.englishName
        } else{
        textView.text = location?.arabicName
        }
    }




}
