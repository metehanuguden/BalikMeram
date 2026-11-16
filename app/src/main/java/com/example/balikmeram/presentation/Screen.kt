package com.example.balikmeram.presentation


sealed class Screen(val route: String, val title: String) {
    object Harita : Screen("harita_screen", "Harita")
    object Meralarim : Screen("meralarim_screen", "Meralarım")
    object Anasayfa : Screen("anasayfa_screen", "Profil") // Yeni eklenen ana üs
    object Avlarim : Screen("avlarim_screen", "Avlarım")
    object HavaDurumu : Screen("havadurumu_screen", "Hava")
}