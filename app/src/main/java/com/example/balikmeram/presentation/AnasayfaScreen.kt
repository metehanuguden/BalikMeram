package com.example.balikmeram.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AnasayfaScreen(onLogout: () -> Unit) {
    // Firestore'dan gelecek verileri tutacağımız state'ler
    var adSoyad by remember { mutableStateOf("Yükleniyor...") }
    var kullaniciAdi by remember { mutableStateOf("") }

    // Gece Mavisi Renk Paleti
    val geceMavisi = Color(0xFF0F172A)
    val acikMavi = Color(0xFF38BDF8)
    val yaziRengi = Color(0xFFF1F5F9)
    val kartRengi = Color(0xFF1E293B)
    val hataRengi = Color(0xFFEF4444) // Çıkış butonu için kırmızı

    // Sayfa açıldığında Firestore'dan kullanıcının verilerini çekiyoruz
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("Kullanicilar").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        adSoyad = document.getString("adSoyad") ?: "İsimsiz Balıkçı"
                        kullaniciAdi = document.getString("kullaniciAdi") ?: ""
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(geceMavisi)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Profil Fotoğrafı
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(kartRengi),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profil",
                tint = Color.Gray,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ad Soyad ve Kullanıcı Adı
        Text(
            text = "Merhaba, $adSoyad",
            color = yaziRengi,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        if (kullaniciAdi.isNotEmpty()) {
            Text(
                text = "@$kullaniciAdi",
                color = acikMavi,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Gelecek için İstatistik Kartı Placeholder'ı
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = kartRengi),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Av İstatistikleri",
                    color = yaziRengi,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Buraya ileride kaydettiğin çipura, palamut ve kasna gibi avlarının istatistikleri ve favori meraların gelecek.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }

        // Boşluğu doldurup butonu en alta itiyoruz
        Spacer(modifier = Modifier.weight(1f))

        // Çıkış Yap Butonu
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut() // Firebase'den çık
                onLogout() // MainActivity'e haber ver (giriş ekranına dön)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = hataRengi)
        ) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Çıkış Yap",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Çıkış Yap",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}