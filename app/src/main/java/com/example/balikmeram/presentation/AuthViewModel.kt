package com.example.balikmeram.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Firestore bağlantısını başlattık
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Giriş yapma fonksiyonu (E-posta doğrulanmış mı denetler)
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Lütfen e-posta ve şifrenizi girin.")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email.trim(), pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user?.isEmailVerified == true) {
                    _authState.value = AuthState.Success("Giriş başarılı!")
                } else {
                    _authState.value = AuthState.Error("Lütfen e-posta adresinize gelen doğrulama bağlantısına tıklayın.")
                    auth.signOut() // Doğrulanmamışsa oturumu kapat
                }
            } else {
                _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Giriş başarısız oldu.")
            }
        }
    }

    // Kayıt olma fonksiyonu (Ad-Soyad ve Kullanıcı Adı ile Firestore'a kayıt yapar)
    fun register(email: String, pass: String, adSoyad: String, kullaniciAdi: String) {
        if (email.isBlank() || pass.isBlank() || adSoyad.isBlank() || kullaniciAdi.isBlank()) {
            _authState.value = AuthState.Error("Lütfen tüm alanları doldurun.")
            return
        }
        if (pass.length < 6) {
            _authState.value = AuthState.Error("Şifre en az 6 karakter olmalıdır.")
            return
        }
        _authState.value = AuthState.Loading

        // 1. Aşama: Firebase Auth ile kullanıcı oluştur
        auth.createUserWithEmailAndPassword(email.trim(), pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid

                if (uid != null) {
                    // 2. Aşama: Firestore'a kaydedilecek verileri hazırla
                    val userMap = hashMapOf(
                        "adSoyad" to adSoyad.trim(),
                        "kullaniciAdi" to kullaniciAdi.trim(),
                        "email" to email.trim()
                    )

                    // 3. Aşama: Verileri Firestore'daki "Kullanicilar" tablosuna uid ile kaydet
                    firestore.collection("Kullanicilar").document(uid).set(userMap)
                        .addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                user.sendEmailVerification()
                                _authState.value = AuthState.Success("Kayıt oluşturuldu! E-postanıza doğrulama bağlantısı gönderildi.")
                                auth.signOut()
                            } else {
                                _authState.value = AuthState.Error(firestoreTask.exception?.localizedMessage ?: "Profil bilgileri kaydedilemedi.")
                            }
                        }
                }
            } else {
                _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Kayıt başarısız oldu.")
            }
        }
    }

    // Şifre sıfırlama e-postası gönderme
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Şifre sıfırlama için e-posta adresinizi yazın.")
            return
        }
        _authState.value = AuthState.Loading
        auth.sendPasswordResetEmail(email.trim()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Success("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.")
            } else {
                _authState.value = AuthState.Error("Şifre sıfırlama e-postası gönderilemedi.")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun isUserLoggedInAndVerified(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null && currentUser.isEmailVerified
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}