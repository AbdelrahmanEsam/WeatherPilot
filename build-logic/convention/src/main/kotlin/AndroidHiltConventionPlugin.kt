

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

class AndroidHiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target)
        {
            val libs = the<LibrariesForLibs>()
            pluginManager.apply {

                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
                apply("dagger.hilt.android.plugin")
            }



            dependencies {

                val dependencies = mapOf(
                    "implementation" to listOf(
                        libs.bundles.hilt
                    ),
                    "kapt" to listOf(
                        libs.androidx.room.compiler,
                        libs.com.google.dagger.hilt.compiler,
                        libs.androidx.hilt.hilt.compiler
                    ),
                    "kaptTest" to listOf(
                        libs.com.google.dagger.hilt.android.compiler,
                        libs.com.google.dagger.hilt.android.testing
                    ),
                    "kaptAndroidTest" to listOf(
                        libs.com.google.dagger.hilt.android.compiler
                    ),
                    "androidTestImplementation" to listOf(
                        libs.com.google.dagger.hilt.android.testing
                    )
                )

                dependencies.forEach { (config, deps) ->
                    deps.forEach {
                        add(config, it)
                    }
                }
            }


        }
    }
}