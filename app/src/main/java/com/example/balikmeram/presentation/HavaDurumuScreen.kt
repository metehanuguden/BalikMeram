package com.example.balikmeram.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HavaDurumuScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var locationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var locationTitle by remember { mutableStateOf("Konum Aranıyor...") }
    var searchQuery by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!locationGranted) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    LaunchedEffect(locationGranted) {
        if (locationGranted) {
            fetchCurrentLocation(context, fusedLocationClient, viewModel) { name -> locationTitle = name }
        } else {
            viewModel.fetchWeather(40.1042, 27.6536)
            locationTitle = "Gönen, Balıkesir"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(locationTitle) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        if (locationGranted) {
                            locationTitle = "Konum Bulunuyor..."
                            fetchCurrentLocation(context, fusedLocationClient, viewModel) { name -> locationTitle = name }
                        } else {
                            viewModel.fetchWeather(40.1042, 27.6536)
                            locationTitle = "Gönen, Balıkesir"
                        }
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Konumuma Dön")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchLocation(it)
                    },
                    label = { Text("Mera ara (Örn: Antal...)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.clearSearch()
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Temizle")
                            }
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Ara")
                        }
                    }
                )

                if (searchResults.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                            items(searchResults) { result ->
                                val placeName = "${result.name}${if(result.admin1 != null) ", ${result.admin1}" else ""}"

                                ListItem(
                                    headlineContent = { Text(placeName, fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text(result.country ?: "") },
                                    modifier = Modifier.clickable {
                                        locationTitle = placeName
                                        viewModel.fetchWeather(result.latitude, result.longitude)
                                        searchQuery = ""
                                        viewModel.clearSearch()
                                        focusManager.clearFocus()
                                    },
                                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                when (uiState) {
                    is WeatherUiState.Loading -> CircularProgressIndicator()
                    is WeatherUiState.Success -> {
                        val weather = (uiState as WeatherUiState.Success).weather.current
                        WeatherCard(weather = weather)
                    }
                    is WeatherUiState.Error -> {
                        val message = (uiState as WeatherUiState.Error).message
                        Text(text = message, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun fetchCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: WeatherViewModel,
    onLocationNameFetched: (String) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            viewModel.fetchWeather(location.latitude, location.longitude)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val geocoder = Geocoder(context, Locale("tr", "TR"))
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val ilce = address.subAdminArea ?: address.locality ?: ""
                        val il = address.adminArea ?: ""
                        val formattedName = when {
                            ilce.isNotEmpty() && il.isNotEmpty() -> "$ilce, $il"
                            ilce.isNotEmpty() -> ilce
                            il.isNotEmpty() -> il
                            else -> "Bilinmeyen Bölge"
                        }
                        withContext(Dispatchers.Main) { onLocationNameFetched(formattedName) }
                    } else {
                        withContext(Dispatchers.Main) { onLocationNameFetched("Adres Bulunamadı") }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onLocationNameFetched("Koordinat: ${String.format("%.2f", location.latitude)}, ${String.format("%.2f", location.longitude)}")
                    }
                }
            }
        } else {
            viewModel.fetchWeather(40.1042, 27.6536)
            onLocationNameFetched("Gönen, Balıkesir")
        }
    }
}

// ---  HAVA DURUMU KARTI ---
@Composable
fun WeatherCard(weather: com.example.balikmeram.data.CurrentWeather) {

    // Hava durumuna göre profesyonel ikon ve metin ataması
    val (weatherIcon, conditionText) = when(weather.weatherCode) {
        0 -> Icons.Rounded.WbSunny to "Açık / Güneşli"
        1, 2, 3 -> Icons.Rounded.Cloud to "Parçalı Bulutlu"
        45, 48 -> Icons.Rounded.Dehaze to "Sisli"
        51, 53, 55, 61, 63, 65 -> Icons.Rounded.WaterDrop to "Yağmurlu"
        71, 73, 75 -> Icons.Rounded.AcUnit to "Karlı"
        95, 96, 99 -> Icons.Rounded.Thunderstorm to "Fırtınalı"
        else -> Icons.Rounded.Thermostat to "Bilinmiyor"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Çok daha yumuşak, modern köşeler
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    // Turkuazdan Laciverte uzanan şık arka plan degradesi (Gradient)
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0EA5E9), Color(0xFF0F172A))
                    )
                )
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ÜST KISIM: Dev İkon ve Derece
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = weatherIcon,
                        contentDescription = conditionText,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp) // KOCAMAN İKON
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${weather.temperature}°",
                        fontSize = 56.sp, // Çok büyük modern derece yazısı
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ORTA KISIM: Durum Yazısı
                Text(
                    text = conditionText,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ALT KISIM: Rüzgar ve Basınç Detayları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherItemModern(
                        title = "Rüzgar",
                        value = "${weather.windSpeed} km/s",
                        icon = Icons.Rounded.Air // Modern rüzgar ikonu
                    )
                    WeatherItemModern(
                        title = "Basınç",
                        value = "${weather.pressure} hPa",
                        icon = Icons.Rounded.Speed // Modern basınç/hız ikonu
                    )
                }
            }
        }
    }
}

// Alt Detay Öğesi
@Composable
fun WeatherItemModern(title: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}