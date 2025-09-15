plugins {
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.windrr.mindbank"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.windrr.mindbank"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "1.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        outputs.all {
            if (buildType.name == "release") {
                val newName = "app-${name}_${versionName}($versionCode).aab"
                (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = newName
            }
        }
    }
}

dependencies {
    implementation("androidx.glance:glance:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation(platform("com.google.firebase:firebase-bom:31.2.3"))
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.3.5")
    implementation("com.google.firebase:firebase-analytics-ktx:21.2.0")
    implementation(libs.tedpermission.normal)
    implementation(libs.tedpermission.coroutine)
    implementation(libs.androidx.biometric)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.timber)
    implementation(libs.androidx.activity.compose.v180alpha07)
    implementation(libs.accompanist.flowlayout)
    implementation("androidx.navigation:navigation-compose:2.8.1")
    implementation("com.github.skydoves:colorpicker-compose:1.1.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation(libs.androidx.appcompat)
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.glance.appwidget)
}

kapt {
    correctErrorTypes = true
}