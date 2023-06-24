package com.example.weatherpilot.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.example.weatherpilot.R


object LoadingImage{

@JvmStatic
@BindingAdapter("imageUrl")
fun loadImage(image: LottieAnimationView, url: String?) {
    url?.let {
      val resource : Int =   when(it.slice(0..1))
        {

            "01" -> R.raw.splash
            "02" -> R.drawable.weather_state
            "03" -> R.drawable.weather_state
            "04" -> R.drawable.weather_state
            "05" -> R.drawable.weather_state
            "06" -> R.drawable.weather_state
            "07" -> R.drawable.weather_state
            "08" -> R.drawable.weather_state
            "09" -> R.drawable.weather_state
            "10" -> R.drawable.weather_state
            "11" -> R.drawable.weather_state
            "13" -> R.drawable.weather_state
            "50" -> R.drawable.weather_state


          else -> R.drawable.weather_state
      }
//        Glide.with(image.context)
//            .load(resource)
//            .override(150, 120).downsample(DownsampleStrategy.CENTER_INSIDE)
//            .into(image)

        image.setAnimation(resource)


    }
}
}
