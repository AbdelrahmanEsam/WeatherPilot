

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.Packaging
import com.example.buildlogic.configureKotlinAndroid
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.NamedDomainObjectCollectionDelegateProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.the

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target)
        {
            val libs = the<LibrariesForLibs>()

            pluginManager.apply {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.kapt")
                apply("kotlin-parcelize")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.apply {
                    applicationId = libs.versions.applicationId.get()
                    minSdk = libs.versions.minSDK.get().toInt()
                    targetSdk = libs.versions.targetSDK.get().toInt()
                    versionCode =  libs.versions.codeVersion.get().toInt()
                    versionName = libs.versions.codeVersion.get()
                    resourceConfigurations.addAll(listOf("en","ar"))
                    testInstrumentationRunner  = "com.example.weatherpilot.HiltTestRunner"
                }

                @Suppress("UNUSED_EXPRESSION")
                fun Packaging.() {
                    resources {
                        excludes.add("/META-INF/{AL2.0,LGPL2.1}")
                    }
                }

                namespace =  libs.versions.applicationNameSpace.get()

            }



          dependencies {


              val dependencies = mapOf(
                  "implementation" to listOf(
                      libs.androidx.core.ktx,
                      libs.androidx.appcompat.appcompat,
                      libs.com.google.android.material.material,
                      libs.androidx.constraintlayout,
                      libs.bundles.navigation.component,
                      libs.bundles.location.maps,
                      libs.bundles.retrofit,
                      libs.bundles.okhttp,
                      libs.bundles.data.store,
                      libs.com.github.bumtech.glide.glide,
                      libs.androidx.swiperefreshlayout.swiperefreshlayout,
                      libs.bundles.sdp,
                      libs.bundles.room,
                      libs.androidx.core.core.splashscreen,
                      libs.com.airbnb.android.lottie
                  ),
                  "kapt" to listOf(
                      libs.androidx.room.compiler
                  ),
                  "testImplementation" to listOf(
                      libs.junit,
                      libs.org.robolectric.robolectric,
                      libs.org.mockito.kotlin.mockito.kotlin,
                      libs.bundles.hamcrest,
                      libs.org.jetbrains.kotlinx.kotlinx.coroutines.test
                  ),
                  "androidTestImplementation" to listOf(
                      libs.org.robolectric.robolectric,
                      libs.bundles.hamcrest,
                      libs.org.jetbrains.kotlinx.kotlinx.coroutines.test,
                      libs.androidx.test.ext,
                      libs.androidx.test.espresso
                  ),
                  "coreLibraryDesugaring" to listOf(
                      libs.com.android.tools.desugar
                  ))

              dependencies.forEach { (config, deps) ->
                  deps.forEach {
                      add(config, it)
                  }
              }

          }

        }
    }



}