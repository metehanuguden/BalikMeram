package com.example.balikmeram.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.balikmeram.data.MeraEntity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: MeraViewModel) {
    val meralarList by viewModel.meralar.collectAsState()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(LatLng(39.0, 35.0), 5f) }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val cancellationToken = CancellationTokenSource().token
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 14f)
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var clickedLocation by remember { mutableStateOf<LatLng?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedMeraForDetail by remember { mutableStateOf<MeraEntity?>(null) }
    var isSatellite by remember { mutableStateOf(false) }
    val mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission, mapType = mapType),
            uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission, zoomControlsEnabled = true),
            onMapClick = { latLng ->
                clickedLocation = latLng
                showAddDialog = true
            },
            onMyLocationButtonClick = {
                if (hasLocationPermission) {
                    try {
                        val cancellationToken = CancellationTokenSource().token
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken)
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
                                        )
                                    }
                                } else {
                                    Toast.makeText(context, "Lütfen cihazınızın Konum (GPS) özelliğini açın.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                true
            }
        ) {
            meralarList.forEach { mera ->
                val meraPosition = LatLng(mera.latitude, mera.longitude)
                val markerState = remember(mera.id) { MarkerState(position = meraPosition) }

                Marker(
                    state = markerState,
                    title = mera.title,
                    snippet = mera.description,
                    onClick = {
                        selectedMeraForDetail = mera
                        true
                    }
                )
            }
        }

        Button(
            onClick = { isSatellite = !isSatellite },
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(text = if (isSatellite) "Normal Harita" else "Uydu Görünümü", fontWeight = FontWeight.Bold)
        }

        // ---  "MERA EKLE" PENCERESİ ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        text = "Yeni Mera Ekle",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        val textFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0EA5E9),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedLabelColor = Color(0xFF0EA5E9),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color(0xFF0EA5E9),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                            focusedContainerColor = Color(0xFF0EA5E9).copy(alpha = 0.05f)
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Mera Adı") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Açıklama") },
                            modifier = Modifier.fillMaxWidth().height(130.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = textFieldColors
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            clickedLocation?.let { loc -> viewModel.addMera(title, description, loc.latitude, loc.longitude) }
                            showAddDialog = false
                            title = ""
                            description = ""
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                    ) {
                        Text("Kaydet", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("İptal", color = Color.White.copy(alpha = 0.6f))
                    }
                }
            )
        }

        if (selectedMeraForDetail != null) {
            val mera = selectedMeraForDetail!!
            AlertDialog(
                onDismissRequest = { selectedMeraForDetail = null },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mera.title,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { selectedMeraForDetail = null },
                            modifier = Modifier
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = mera.description.ifEmpty { "Açıklama girilmemiş." },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val uri = "google.navigation:q=${mera.latitude},${mera.longitude}".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.setPackage("com.google.android.apps.maps")

                                // Cihazda Haritalar uygulamasının varlığını denetleyen güvenli blok
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    val browserUri = "https://www.google.com/maps/dir/?api=1&destination=${mera.latitude},${mera.longitude}".toUri()
                                    val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                                    context.startActivity(browserIntent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.85f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = "Yol Tarifi")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yol Tarifi Al", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}