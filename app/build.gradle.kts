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
            applicationIdSuffix = WagbatBuildTypes.DEBUG.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        }



        val release by getting {
            isMinifyEnabled = true
            applicationIdSuffix = WagbatBuildTypes.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        forEach {
            it.buildConfigField("String","API_KEY","\"1a63fd6a655de75849051be17b899886\"")
            it.buildConfigField("String","API_BASE","\"https://api.openweathermap.org/\"")
        }

    }
}

dependencies {

}