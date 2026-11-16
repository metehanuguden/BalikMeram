package com.example.balikmeram.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    //  Renk Paleti
    val geceMavisi = Color(0xFF0F172A)
    val acikMavi = Color(0xFF38BDF8)
    val yaziRengi = Color(0xFFF1F5F9)

    // ViewModel'dan gelen durumları dinliyoruz
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val message = (authState as AuthState.Success).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onNavigateBack() // E-posta gidince login ekranına geri dön
            }
            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Şifremi Unuttum", color = yaziRengi) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri Dön",
                            tint = yaziRengi
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = geceMavisi)
            )
        },
        containerColor = geceMavisi
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Şifrenizi mi unuttunuz?",
                color = yaziRengi,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kayıtlı e-posta adresinizi girin. Size şifre sıfırlama bağlantısı göndereceğiz.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // E-posta Kutusu
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta Adresiniz", color = Color.Gray) },
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
            Spacer(modifier = Modifier.height(24.dp))

            // Sıfırlama Butonu
            Button(
                onClick = { viewModel.resetPassword(email) },
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
                        text = "Sıfırlama Bağlantısı Gönder",
                        color = geceMavisi,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}