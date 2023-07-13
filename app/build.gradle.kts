import com.example.buildlogic.WagbatBuildTypes
plugins {
    id("example.android.application")
    id("androidx.navigation.safeargs")
    id("example.android.hilt")
}

android {
    namespace = libs.versions.applicationNameSpace.get()
    compileSdk  = libs.versions.compileSDK.get().toInt()

    buildTypes {
        val debug by getting {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        }

        val release by getting {
            isMinifyEnabled = true
            applicationIdSuffix = WagbatBuildTypes.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        forEach {
            it.buildConfigField("String","API_KEY","\"d0ad5cd60a7ee75e6215769e9f2c10bf\"")
            it.buildConfigField("String","API_BASE","\"https://api.openweathermap.org/\"")
        }

    }
}

dependencies {

}