package com.example.balikmeram.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    // Ekran durumları
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") } // Yeni: Şifre Tekrar

    // Yeni: Göz ikonu için durumlar
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }

    // Yeni: Ad ve Soyad ayrı kutular için
    var ad by remember { mutableStateOf("") }
    var soyad by remember { mutableStateOf("") }

    var kullaniciAdi by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Gece Mavisi Renk Paleti
    val geceMavisi = Color(0xFF0F172A)
    val acikMavi = Color(0xFF38BDF8)
    val yaziRengi = Color(0xFFF1F5F9)
    val hataRengi = Color(0xFFEF4444) // Kırmızı uyarı rengi

    // Şifrelerin uyuşup uyuşmadığını kontrol
    val passwordsMatch = password == passwordConfirm

    // ViewModel'dan gelen durumları dinliyoruz
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val message = (authState as AuthState.Success).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (isLogin) {
                    onNavigateToHome()
                } else {
                    isLogin = true
                    email = ""
                    password = ""
                    passwordConfirm = ""
                    ad = ""
                    soyad = ""
                    kullaniciAdi = ""
                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(geceMavisi)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo / Başlık
        Text(
            text = "BalıkMeram",
            color = yaziRengi,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isLogin) "Rastgele! Hesabınıza giriş yapın." else "Aramıza katılın, meraları keşfedin.",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Kayıt modundaysak Ad-Soyad (Yan yana) ve Kullanıcı Adı kutularını göster
        if (!isLogin) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = ad,
                    onValueChange = { ad = it },
                    label = { Text("Ad", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = acikMavi,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = yaziRengi,
                        unfocusedTextColor = yaziRengi
                    ),
                    modifier = Modifier.weight(1f) // Ekranı yarı yarıya paylaşırlar
                )

                OutlinedTextField(
                    value = soyad,
                    onValueChange = { soyad = it },
                    label = { Text("Soyad", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = acikMavi,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = yaziRengi,
                        unfocusedTextColor = yaziRengi
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = kullaniciAdi,
                onValueChange = { kullaniciAdi = it },
                label = { Text("Kullanıcı Adı", color = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = acikMavi,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = yaziRengi,
                    unfocusedTextColor = yaziRengi
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // E-posta Kutusu
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta", color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = acikMavi,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = yaziRengi,
                unfocusedTextColor = yaziRengi
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Şifre Kutusu
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre", color = Color.Gray) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Şifreyi Göster", tint = Color.Gray)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = acikMavi,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = yaziRengi,
                unfocusedTextColor = yaziRengi
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Şifre Tekrar Kutusu (Sadece Kayıt Modunda)
        if (!isLogin) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                label = { Text("Şifre Tekrar", color = Color.Gray) },
                singleLine = true,
                visualTransformation = if (passwordConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordConfirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordConfirmVisible = !passwordConfirmVisible }) {
                        Icon(imageVector = image, contentDescription = "Şifreyi Göster", tint = Color.Gray)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (!passwordsMatch && passwordConfirm.isNotEmpty()) hataRengi else acikMavi,
                    unfocusedBorderColor = if (!passwordsMatch && passwordConfirm.isNotEmpty()) hataRengi else Color.Gray,
                    focusedTextColor = yaziRengi,
                    unfocusedTextColor = yaziRengi
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Eğer şifre tekrar kutusu boş değilse ve şifreler uyuşmuyorsa kırmızı uyarı ver
            if (passwordConfirm.isNotEmpty() && !passwordsMatch) {
                Text(
                    text = "Şifreler uyuşmuyor.",
                    color = hataRengi,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        // Şifremi Unuttum Butonu
        if (isLogin) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text(text = "Şifremi Unuttum?", color = acikMavi, fontSize = 13.sp)
                }
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Ana Buton (Giriş / Kayıt Ol)
        Button(
            onClick = {
                if (isLogin) {
                    viewModel.login(email, password)
                } else {
                    // Kayıt olurken önce şifrelerin eşleştiğinden emin oluyoruz
                    if (passwordsMatch) {
                        // Ad ve Soyad'ı birleştirip ViewModel'a gönderiyoruz
                        val birlesikAdSoyad = "${ad.trim()} ${soyad.trim()}"
                        viewModel.register(email, password, birlesikAdSoyad, kullaniciAdi)
                    } else {
                        Toast.makeText(context, "Lütfen şifrelerin uyuştuğundan emin olun.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = acikMavi)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = geceMavisi, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (isLogin) "Giriş Yap" else "Kayıt Ol",
                    color = geceMavisi,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Alt Geçiş Metni
        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                text = if (isLogin) "Hesabın yok mu? Yeni Hesap Oluştur" else "Zaten hesabın var mı? Giriş Yap",
                color = Color.LightGray
            )
        }
    }
}