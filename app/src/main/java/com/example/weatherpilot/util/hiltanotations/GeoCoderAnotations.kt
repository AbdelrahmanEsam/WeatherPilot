package com.example.weatherpilot.util.hiltanotations

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class GeoCoder(val geoCoder: GeoCoders)


enum class GeoCoders{
    ArabicGeoCoder,
    EnglishGeoCoder,
}