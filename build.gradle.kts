// libs.plugins... yapılarını tamamen çöpe attık! Doğrudan id ve versiyonlarını yazıyoruz.
plugins {
    id("com.android.application") version "8.7.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}