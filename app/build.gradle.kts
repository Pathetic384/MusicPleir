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
    implementation(libs.espresso.contrib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp library
    implementation ("com.github.bumptech.glide:glide:4.13.0") // Glide library
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0") // Glide annotation processor
}