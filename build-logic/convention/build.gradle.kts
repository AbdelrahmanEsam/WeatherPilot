plugins {
    `kotlin-dsl`

}

group = "com.wagbat.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)

    // Enables using type-safe accessors to reference plugins from the [plugins] block defined in version catalogs.
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {

    fun createPlugin(id: String, className: String) {
        plugins.create(id) {
            this.id = id
            implementationClass = className
        }
    }
    plugins {
        createPlugin("example.android.hilt","AndroidHiltConventionPlugin")
        createPlugin("example.android.library","AndroidLibraryConventionPlugin")
        createPlugin("example.android.application","AndroidApplicationConventionPlugin")

    }
}
