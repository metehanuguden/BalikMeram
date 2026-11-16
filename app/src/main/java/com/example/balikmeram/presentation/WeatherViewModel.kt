package com.example.balikmeram.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balikmeram.data.GeocodingResult
import com.example.balikmeram.data.RetrofitInstance
import com.example.balikmeram.data.WeatherResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    // Canlı arama sonuçlarını tutacağımız liste
    private val _searchResults = MutableStateFlow<List<GeocodingResult>>(emptyList())
    val searchResults: StateFlow<List<GeocodingResult>> = _searchResults

    private var searchJob: Job? = null

    fun fetchWeather(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = RetrofitInstance.api.getCurrentWeather(lat, lng)
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("Bağlantı hatası: İnternetini kontrol et.")
            }
        }
    }

    // Arama kutusuna her harf basıldığında çalışacak fonksiyon
    fun searchLocation(query: String) {
        searchJob?.cancel() // Kullanıcı hızlı hızlı yazıyorsa önceki aramaları iptal et

        if (query.length < 2) {
            _searchResults.value = emptyList() // En az 2 harf yazılmadıysa listeyi boşalt
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Klavyede yazma işleminin bitmesini 300ms bekle
            try {
                val response = RetrofitInstance.api.searchLocation(query)
                _searchResults.value = response.results ?: emptyList()
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }
}