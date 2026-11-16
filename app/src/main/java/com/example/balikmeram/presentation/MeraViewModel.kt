package com.example.balikmeram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.balikmeram.data.MeraDao
import com.example.balikmeram.data.MeraEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MeraViewModel(private val dao: MeraDao) : ViewModel() {

    val meralar = dao.getAllMeralar()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMera(title: String, description: String, lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMera(
                MeraEntity(
                    title = title,
                    description = description,
                    latitude = lat,
                    longitude = lng
                )
            )
        }
    }

    // Listeden meraları silebilmek için  fonksiyon
    fun deleteMera(mera: MeraEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMeraById(mera.id)
        }
    }
}

class MeraViewModelFactory(private val dao: MeraDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeraViewModel(dao) as T
        }
        throw IllegalArgumentException("Bilinmeyen ViewModel sınıfı")
    }
}