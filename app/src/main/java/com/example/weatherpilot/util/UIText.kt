package com.example.weatherpilot.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UIText
{
    data class DynamicText(val text : String) : UIText()
    data class ResourceText(@StringRes val  id : Int) : UIText()



    fun asString(context : Context)  :String
    {
        return when(this){
            is DynamicText -> text
            is ResourceText -> context.getString(id)
        }
    }
}



