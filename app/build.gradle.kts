plugins {

    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.balikmeram"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.balikmeram"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {


    // Temel Android ve Compose Kütüphaneleri
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    // FIREBASE AUTH HATASINI ÇÖZEN STABİL SÜRÜM
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")

    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Sayfa Geçişi (Navigation)
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Harita (Google Maps)
    implementation("com.google.maps.android:maps-compose:6.1.2")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Retrofit ve Gson (İnternetten veri çekmek için)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Veritabanı (Room Database)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // İzin yönetimi için Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // Cihazın anlık GPS konumunu almak için
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // Fotoğrafları ekranda göstermek için Coil kütüphanesi
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Modern Hava Durumu İkonları İçin
    implementation("androidx.compose.material:material-icons-extended")
}