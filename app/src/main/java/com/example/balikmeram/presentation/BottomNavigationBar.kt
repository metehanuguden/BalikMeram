package com.example.balikmeram.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SetMeal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Alt menü öğeleri
data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Anasayfa (Profil) sekmesi tam ortaya (3. sıraya) yerleştirildi
    val items = listOf(
        BottomNavItem(Screen.Harita.route, Screen.Harita.title, Icons.Rounded.Map),               // Katlanmış Harita
        BottomNavItem(Screen.Meralarim.route, Screen.Meralarim.title, Icons.Rounded.LocationOn), // Modern Konum Pimi
        BottomNavItem(Screen.Anasayfa.route, Screen.Anasayfa.title, Icons.Rounded.Person),       // Yeni Profil Butonu (Ana Üs)
        BottomNavItem(Screen.Avlarim.route, Screen.Avlarim.title, Icons.Rounded.SetMeal),        // Balık İkonu
        BottomNavItem(Screen.HavaDurumu.route, Screen.HavaDurumu.title, Icons.Rounded.Cloud)    // Şık Bulut
    )

    //  Navigasyon Çubuğu
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                // Seçili olanlara Okyanus Turkuazı parlaklık, seçili olmayanlara soluk modern gri
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}