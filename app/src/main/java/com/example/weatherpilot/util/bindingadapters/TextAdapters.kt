package com.example.weatherpilot.util.bindingadapters

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.model.Location
import com.example.weatherpilot.domain.model.SearchItem
import com.example.weatherpilot.util.usescases.convertNumberEnglishToArabic
import com.example.weatherpilot.util.usescases.toDate


object TextAdapters {

    @JvmStatic
    @BindingAdapter("windWithType")
    fun windWithType(textView: TextView, wind: String?) {
        wind?.let {
            val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
            val result = when (wind.split(" ")[1]) {
                textView.context.getString(R.string.meter_sec_type) -> wind.split(" ")[0].convertNumberEnglishToArabic(convertToArabic) + " " + textView.context.getString(
                    R.string.meter_sec
                )

                else -> wind.split(" ")[0].convertNumberEnglishToArabic(convertToArabic)  + " " + textView.context.getString(R.string.mile_hour)
            }
            textView.text = result
        }
    }


    @JvmStatic
    @BindingAdapter("tempWithType")
    fun tempWithType(textView: TextView, temp: String?) {

        val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
        temp?.let {
            with(textView.context) {

                val result = when (temp.split(" ")[1]) {

                    getString(R.string.fahrenheit_type) -> temp.split(" ")[0].convertNumberEnglishToArabic(
                        convertToArabic
                    ) + " " + getString(R.string.fahrenheit_symbol)

                    getString(R.string.kelvin_type) -> temp.split(" ")[0].convertNumberEnglishToArabic(
                        convertToArabic
                    ) + " " + getString(R.string.kelvin_symbol)


                    else -> temp.split(" ")[0].convertNumberEnglishToArabic(convertToArabic) + " " + getString(
                        R.string.celsius_symbol
                    )
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
    fun setTimer(textView: TextView, alert: AlertItem?) {

        alert?.let {

            textView.text = alert.time.toDate()
        }


    }


    @JvmStatic
    @BindingAdapter("setSearchResultNameWithLang")
    fun setSearchResultNameWithLang(textView: TextView, item: SearchItem?) {

        item?.let {
            textView.text =
                if (textView.context.resources.getBoolean(R.bool.is_english)) item.name else item.LocalNames?.ar
        }
    }


    @JvmStatic
    @BindingAdapter("setSearchResultStateWithLang")
    fun setSearchResultStateWithLang(textView: TextView, item: SearchItem?) {

        item?.let {
            val address = item.state + " " + item.country
            textView.text = address
        }
    }


    @JvmStatic
    @BindingAdapter("setHourWithAMOrPM")
    fun setHourWithAMOrPM(textView: TextView, hour: String) {
        val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
        hour?.let {
            with(textView.context) {

                val result = when (hour.split(" ")[1]) {

                    getString(R.string.am) -> hour.split(" ")[0].convertNumberEnglishToArabic(
                        convertToArabic
                    ) + " " + getString(R.string.am)

                    else -> hour.split(" ")[0].convertNumberEnglishToArabic(convertToArabic) + " " + getString(
                        R.string.pm
                    )
                }
                textView.text = result
            }
        }
    }

    @JvmStatic
    @BindingAdapter("convertEnglishNumberToArabic")
    fun convertEnglishNumberToArabic(textView: TextView, hour: String) {
        val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
        hour.let {
            textView.text = hour.convertNumberEnglishToArabic(convertToArabic)
        }
    }


    @JvmStatic
    @BindingAdapter("min", "max")
    fun setMaxAndMinTemp(textView: TextView, min: String, max: String) {


        val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
        val formatted = buildString {
            append(min.convertNumberEnglishToArabic(convertToArabic))
            append("°")
            append("/")
            append(max.convertNumberEnglishToArabic(convertToArabic))
            append("°")
        }

        textView.text = formatted
        Log.d("min adapter", formatted)


    }


    @JvmStatic
    @BindingAdapter("hpa")
    fun hpa(textView: TextView ,value: String?) {


        value?.let {

        val hpa = textView.context.resources.getString(R.string.hpa)
        val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
        val formatted = buildString {
            append(value.convertNumberEnglishToArabic(convertToArabic))
            append(" ")
            append(hpa)
        }
        textView.text = formatted
        }
    }


    @JvmStatic
    @BindingAdapter("percent")
    fun percent(textView: TextView, value: String?) {


        value?.let {

            val humidity = textView.context.resources.getString(R.string.humidityTextValue)
            val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
            val formatted = buildString {
                append(value.convertNumberEnglishToArabic(convertToArabic))
                append(" ")
                append(humidity)
            }

            textView.text = formatted
        }
    }


    @JvmStatic
    @BindingAdapter("visibility")
    fun visibility(textView: TextView ,value: String?) {


        value?.let {

            val visibility = textView.context.resources.getString(R.string.visibilityTextValue)
            val convertToArabic = !textView.context.resources.getBoolean(R.bool.is_english)
            val formatted = buildString {
                append(value.convertNumberEnglishToArabic(convertToArabic))
                append(" ")
                append(visibility)
            }

            textView.text = formatted
        }
    }


}










