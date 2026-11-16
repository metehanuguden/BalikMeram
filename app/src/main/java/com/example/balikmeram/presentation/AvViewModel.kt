package com.example.balikmeram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.balikmeram.data.AvEntity
import com.example.balikmeram.data.AvDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AvViewModel(private val dao: AvDao) : ViewModel() {
    private val _avlar = MutableStateFlow<List<AvEntity>>(emptyList())
    val avlar: StateFlow<List<AvEntity>> = _avlar

    init {
        loadAvlar()
    }

    private fun loadAvlar() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAllAvlar().collect { liste ->
                _avlar.value = liste
            }
        }
    }

    fun addAv(balikTuru: String, agirlik: String, ekipman: String, imageUri: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertAv(
                AvEntity(
                    balikTuru = balikTuru,
                    agirlik = agirlik,
                    ekipman = ekipman,
                    imageUri = imageUri
                )
            )
        }
    }
}


class AvViewModelFactory(private val dao: AvDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AvViewModel(dao) as T
        }
        throw IllegalArgumentException("Bilinmeyen ViewModel sınıfı")
    }
}