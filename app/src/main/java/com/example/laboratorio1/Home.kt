package com.example.laboratorio1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.laboratorio1.util.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun homeScreen(
    nombre: String,
    apellido: String,
    navController: NavController,
    preferencesManager: PreferencesManager
) {
    val scope = rememberCoroutineScope()
    val isAdmin by preferencesManager.isAdminFlow.collectAsState(initial = false)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "¡Bienvenido, $nombre $apellido!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Badge de Admin
            if (isAdmin) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "ADMINISTRADOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botón Gestionar Usuarios (solo para admins)
            if (isAdmin) {
                Button(
                    onClick = {
                        navController.navigate("userList")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gestionar Usuarios")
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        preferencesManager.setLoggedIn(false)
                    }

                    navController.navigate("login") {
                        popUpTo("home/${nombre}/${apellido}") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
