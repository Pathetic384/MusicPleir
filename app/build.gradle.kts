plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.musicpleir"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.musicpleir"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.glide)
    implementation(libs.palette)
    implementation(libs.media)
    implementation(libs.firebase.auth)
    implementation(libs.runner)
    implementation(libs.rules)
    implementation(libs.mockito.core)
    implementation(libs.core.ktx)
    implementation(libs.monitor)
    implementation(libs.fragment.testing)
    implementation(libs.fragment.testing.manifest)
    implementation(libs.core)
    implementation(libs.uiautomator)
    implementation(libs.androidx.espresso.contrib)
    implementation(libs.androidx.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation ("com.github.bumptech.glide:glide:4.13.0") // Glide library
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0") // Glide annotation processor
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation ("org.json:json:20210307")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("se.michaelthelin.spotify:spotify-web-api-java:6.5.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp library

}