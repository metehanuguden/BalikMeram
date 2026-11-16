package com.example.balikmeram.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

// --- Hava Durumu Modelleri ---
data class WeatherResponse(
    val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("surface_pressure") val pressure: Double,
    @SerializedName("weather_code") val weatherCode: Int
)

// --- Arama (Canlı Öneri) Modelleri  ---
data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val admin1: String?, // İl veya Bölge Adı
    val country: String?
)

// --- API Arayüzü ---
interface WeatherApiService {
    // 1. Hava durumunu çeken uç nokta
    @GET("v1/forecast?current=temperature_2m,wind_speed_10m,surface_pressure,weather_code")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double
    ): WeatherResponse

    // 2. Canlı arama önerilerini getiren uç nokta
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchLocation(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "tr"
    ): GeocodingResponse
}

// Retrofit İstemcisi
object RetrofitInstance {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}