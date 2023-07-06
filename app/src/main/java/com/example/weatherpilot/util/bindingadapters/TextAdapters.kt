package com.example.weatherpilot.util.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.model.SearchItem
import com.example.weatherpilot.util.usescases.toDate


object TextAdapters {

    @JvmStatic
    @BindingAdapter("windWithType")
    fun windWithType(textView: TextView, wind: String?) {
        wind?.let {
            val result = when (wind.split(" ")[1]) {
                textView.context.getString(R.string.meter_sec_type) -> wind.split(" ")[0] + " " + textView.context.getString(
                    R.string.meter_sec
                )

                else -> wind.split(" ")[0] + " " + textView.context.getString(R.string.mile_hour)
            }
            textView.text = result
        }
    }


    @JvmStatic
    @BindingAdapter("tempWithType")
    fun tempWithType(textView: TextView, temp: String?) {
        temp?.let {
            with(textView.context) {

                val result = when (temp.split(" ")[1]) {

                    getString(R.string.fahrenheit_type) -> temp.split(" ")[0] + " " + getString(R.string.fahrenheit_symbol)

                    getString(R.string.kelvin_type) -> temp.split(" ")[0] + " " + getString(R.string.kelvin_symbol)


                    else -> temp.split(" ")[0] + " " + getString(R.string.celsius_symbol)
                }
                textView.text = result
            }
        }
    }


    @JvmStatic
    @BindingAdapter("setArabicOrEnglishLocation")
    fun setArabicOrEnglishLocation(textView: TextView, location: Location?) {
        if (textView.context.resources.getBoolean(R.bool.is_english)) {
            textView.text = location?.englishName
        } else {
            textView.text = location?.arabicName
        }
    }



    @JvmStatic
    @BindingAdapter("setArabicOrEnglishAlert")
    fun setArabicOrEnglishAlert(textView: TextView, alert: AlertItem?) {
        alert?.let {

        if (textView.context.resources.getBoolean(R.bool.is_english)) {
            textView.text = alert.englishName
        } else {
            textView.text = alert.arabicName
        }
        }
    }


    @JvmStatic
    @BindingAdapter("setTimer")
    fun setTimer(textView: TextView, alert: AlertItem?)
    {

        alert?.let {

              textView.text = alert.time.toDate()
        }


    }


    @JvmStatic
    @BindingAdapter("setSearchResultNameWithLang")
    fun setSearchResultNameWithLang(textView: TextView, item: SearchItem?)
    {

        item?.let {
                textView.text =   if (textView.context.resources.getBoolean(R.bool.is_english)) item.name else  item.LocalNames?.ar
        }
    }


    @JvmStatic
    @BindingAdapter("setSearchResultStateWithLang")
    fun setSearchResultStateWithLang(textView: TextView, item: SearchItem?)
    {

        item?.let {
           val address = item.state+" "+ item.country
            textView.text =    address
        }
    }
}
