buildscript {
    repositories {
        google()
    }
    dependencies {
     classpath(libs.androidx.navigation.navigation.safe.args.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.android.application) apply  false
    alias(libs.plugins.android.library) apply  false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}