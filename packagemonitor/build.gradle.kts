plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.launcher.launcherpackage"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    //TODO ("ADS")
    implementation("com.google.android.gms:play-services-ads:23.5.0")

    // TODO("Shimmer")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // TODO("Gson")
    implementation("com.google.code.gson:gson:2.11.0")

    //TODO("Lottie")
    implementation("com.airbnb.android:lottie:6.6.7")

    //TODO("Glide")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // TODO("Firebase")
    implementation("com.google.firebase:firebase-analytics:23.0.0")

    //TODO Meta Marketing
    implementation(libs.facebook.android.sdk)
    implementation(libs.androidx.activity)
}