plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = libs.versions.applicationNameSpace.get()
    compileSdk  = libs.versions.compileSDK.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSDK.get().toInt()
        targetSdk = libs.versions.targetSDK.get().toInt()
        versionCode =  libs.versions.codeVersion.get().toInt()
        versionName = libs.versions.codeVersion.get()

        testInstrumentationRunner  = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        val debug by getting {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        }



        val release by getting {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        forEach {
            it.buildConfigField("String","API_KEY","\"d059d2844b294f1969f350bd265f3026\"")
            it.buildConfigField("String","API_BASE","\"https://api.openweathermap.org/\"")
        }

    }
    compileOptions {
        sourceCompatibility  = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.javaVersion.get()
    }


    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation (libs.androidx.core.ktx)
    implementation (libs.androidx.appcompat.appcompat)
    implementation (libs.com.google.android.material.material)
    implementation (libs.androidx.constraintlayout)
    implementation(libs.bundles.navigation.component)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okhttp)
    implementation(libs.com.github.bumtech.glide.glide)
    implementation(libs.bundles.sdp)
    implementation(libs.bundles.hilt)
    implementation(libs.androidx.core.core.splashscreen)
    implementation(libs.com.airbnb.android.lottie)
    testImplementation (libs.junit)
    androidTestImplementation (libs.androidx.test.ext)
    androidTestImplementation (libs.androidx.test.espresso)
}