plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "2.0.10-1.0.24"
    id("kotlin-parcelize")
}

android {
    namespace = "com.dicoding.dicodingeventapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dicoding.dicodingeventapp"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.picasso)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.filament.android)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.ui.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.glide)
    kapt(libs.glideCompiler)
    implementation(libs.async.http)
    implementation (libs.fragment.ktx)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    kapt ("androidx.room:room-compiler:2.6.1") // KAPT untuk Room
    implementation ("androidx.room:room-runtime:2.6.1") // Runtime Room
    implementation ("androidx.room:room-ktx:2.6.1") // KTX Room
    implementation ("com.google.android.material:material:version_number")
    implementation ("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.work:work-runtime:2.9.1")
    implementation("com.loopj.android:android-async-http:1.4.9")
    implementation ("androidx.work:work-runtime-ktx:2.9.1")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.3.1")


}
