package com.example.balikmeram.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.balikmeram.data.AvEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvlarimScreen(viewModel: AvViewModel) {
    val avlarList by viewModel.avlar.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    var balikTuru by remember { mutableStateOf("") }
    var agirlik by remember { mutableStateOf("") }
    var ekipman by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Av Günlüğüm") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Av Ekle")
            }
        }
    ) { innerPadding ->
        if (avlarList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Henüz bir av kaydetmedin. Rastgele!", color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(avlarList) { av ->
                    AvCard(av = av)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    selectedImageUri = null
                },
                title = { Text("Yeni Av Kaydet") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = balikTuru, onValueChange = { balikTuru = it }, label = { Text("Balık Türü (Örn: Levrek)") })
                        OutlinedTextField(value = agirlik, onValueChange = { agirlik = it }, label = { Text("Ağırlık / Boy (Örn: 2kg)") })
                        OutlinedTextField(value = ekipman, onValueChange = { ekipman = it }, label = { Text("Ekipman (Örn: Mepps Aglia)") })

                        Button(
                            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Galeri")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (selectedImageUri == null) "Fotoğraf Ekle" else "Fotoğrafı Değiştir")
                        }

                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Seçilen Fotoğraf",
                                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.addAv(balikTuru, agirlik, ekipman, selectedImageUri?.toString())
                        showDialog = false
                        balikTuru = ""
                        agirlik = ""
                        ekipman = ""
                        selectedImageUri = null
                    }) { Text("Kaydet") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; selectedImageUri = null }) { Text("İptal") }
                }
            )
        }
    }
}

@Composable
fun AvCard(av: AvEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (av.imageUri != null) {
                AsyncImage(
                    model = Uri.parse(av.imageUri),
                    contentDescription = "Av Fotoğrafı",
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = av.balikTuru, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "⚖️ Ağırlık/Boy: ${av.agirlik}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "🎣 Ekipman: ${av.ekipman}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}