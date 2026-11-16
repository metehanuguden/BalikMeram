package com.example.balikmeram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- VERİTABANI VE VİEWMODEL İMPORTLARI ---
import com.example.balikmeram.data.AppDatabase
import com.example.balikmeram.presentation.AuthViewModel
import com.example.balikmeram.presentation.AvViewModel
import com.example.balikmeram.presentation.AvViewModelFactory
import com.example.balikmeram.presentation.LoginScreen
import com.example.balikmeram.presentation.ForgotPasswordScreen
import com.example.balikmeram.presentation.MeraViewModel
import com.example.balikmeram.presentation.MeraViewModelFactory
import com.example.balikmeram.presentation.WeatherViewModel

// --- EKRAN İMPORTLARI ---
import com.example.balikmeram.presentation.AnasayfaScreen // YENİ EKLENDİ
import com.example.balikmeram.presentation.AvlarimScreen
import com.example.balikmeram.presentation.BottomNavigationBar
import com.example.balikmeram.presentation.HavaDurumuScreen
import com.example.balikmeram.presentation.MapScreen
import com.example.balikmeram.presentation.MeralarimScreen
import com.example.balikmeram.presentation.Screen
import com.example.balikmeram.ui.theme.BalikMeramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BalikMeramTheme {

                // 1. Firebase Oturum Yönetimi
                val authViewModel: AuthViewModel = viewModel()
                var isLoggedIn by remember { mutableStateOf(authViewModel.isUserLoggedInAndVerified()) }
                // Şifremi Unuttum ekranı için state
                var showForgotPassword by remember { mutableStateOf(false) }

                // EĞER KULLANICI GİRİŞ YAPMAMIŞSA (VEYA DOĞRULANMAMIŞSA)
                if (!isLoggedIn) {
                    if (showForgotPassword) {
                        // Şifremi Unuttum Ekranı
                        ForgotPasswordScreen(
                            viewModel = authViewModel,
                            onNavigateBack = { showForgotPassword = false } // Geri dönünce login'i göster
                        )
                    } else {
                        // Giriş Ekranı
                        LoginScreen(
                            viewModel = authViewModel,
                            onNavigateToHome = { isLoggedIn = true }, // Giriş başarılı olunca ana sayfaya yönlendir
                            onNavigateToForgotPassword = { showForgotPassword = true } // Şifremi unuttuma basılınca
                        )
                    }
                }
                // KULLANICI BAŞARIYLA GİRİŞ YAPTIYSA ANA UYGULAMAYI YÜKLE
                else {
                    val navController = rememberNavController()

                    // Veritabanını başlatıyoruz
                    val database = AppDatabase.getDatabase(applicationContext)

                    // Meralarım için Dao ve ViewModel
                    val meraDao = database.meraDao()
                    val meraViewModel: MeraViewModel = viewModel(
                        factory = MeraViewModelFactory(meraDao)
                    )

                    // Avlarım için Dao ve ViewModel
                    val avDao = database.avDao()
                    val avViewModel: AvViewModel = viewModel(
                        factory = AvViewModelFactory(avDao)
                    )

                    // Hava Durumu için ViewModel
                    val weatherViewModel: WeatherViewModel = viewModel()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Harita.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            // Harita Ekranı
                            composable(Screen.Harita.route) {
                                MapScreen(viewModel = meraViewModel)
                            }

                            // Meralarım Ekranı
                            composable(Screen.Meralarim.route) {
                                MeralarimScreen(viewModel = meraViewModel)
                            }

                            // Anasayfa (Profil) Ekranı - YENİ EKLENDİ
                            composable(Screen.Anasayfa.route) {
                                AnasayfaScreen(
                                    onLogout = { isLoggedIn = false } // Çıkış yapıldığında giriş ekranına döndürür
                                )
                            }

                            // Avlarım Ekranı
                            composable(Screen.Avlarim.route) {
                                AvlarimScreen(viewModel = avViewModel)
                            }

                            // Hava Durumu Ekranı
                            composable(Screen.HavaDurumu.route) {
                                HavaDurumuScreen(viewModel = weatherViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Eski geçici (Placeholder) ekran tasarımı
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}