package com.example.balikmeram.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// uygulamanın yazı tipi stili
val Typography = Typography(
    // Büyük Başlıklar (Örn: Üst çubuk ve kart başlıkları)
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold, // Çok kalın modern başlık
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),
    // Orta Başlıklar
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold, // Kalın başlık
        fontSize = 18.sp,
        letterSpacing = 0.15.sp
    ),
    // Normal Yazılar (Örn: Açıklamalar)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Buton Yazıları
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold, // Buton yazıları kalın ve net
        fontSize = 15.sp,
        letterSpacing = 1.sp
    )
)