package com.example.weatherpilot.util

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.example.weatherpilot.R


object LoadingImage{

@JvmStatic
@BindingAdapter("imageUrl")
fun loadLottieImage(image: LottieAnimationView, url: String?) {
    url?.let {
      val resource : Int =   when(it.slice(0..1))
        {

            "01" -> R.raw.sunny
            "02" -> R.raw.sun_with_clouds
            "03","04" -> {image.apply{setMaxFrame(70)};R.raw.cloud}
            "09","10" -> R.raw.cloudyrain
            "11" -> R.raw.thunderstorm
            "13" -> R.raw.snow
            "50" -> R.raw.mist
            else -> R.raw.sunny
      }
        image.setAnimation(resource)



    }
}



    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(image: ImageView, url: String?) {
        Log.d("image",url.toString())
            Glide.with(image.context)
                .load("https://openweathermap.org/img/wn/${url}@2x.png")
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .into(image)

    }




        @JvmStatic
        @BindingAdapter("setIntegerText")
        fun setIntegerText(stringText: TextView, text: Int?) {
            text?.let {
                stringText.text = it.toString()
            }
        }
}
