plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
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
        resourceConfigurations.addAll(listOf("en","ar"))
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
            it.buildConfigField("String","API_KEY","\"1a63fd6a655de75849051be17b899886\"")
            it.buildConfigField("String","API_BASE","\"https://api.openweathermap.org/\"")
        }

    }
    compileOptions {
        sourceCompatibility  = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
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
    implementation(libs.bundles.location.maps)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.data.store)
    implementation(libs.com.github.bumtech.glide.glide)
    implementation(libs.bundles.sdp)
    implementation(libs.bundles.hilt)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.core.core.splashscreen)
    implementation(libs.com.airbnb.android.lottie)
    testImplementation (libs.junit)
    androidTestImplementation (libs.androidx.test.ext)
    androidTestImplementation (libs.androidx.test.espresso)
    coreLibraryDesugaring(libs.com.android.tools.desugar)
}