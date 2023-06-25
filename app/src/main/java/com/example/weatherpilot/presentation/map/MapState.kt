package com.example.weatherpilot.presentation.map

data class MapState( var longitude : String = ""
                     , var latitude : String = ""
                     ,var saveState :Boolean?  = null
                      , var mapLoadingState : Boolean  = true
                     )
