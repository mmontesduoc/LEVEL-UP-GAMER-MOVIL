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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke

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
        Image(
            // --- Usa el nombre de tu imagen de fondo aquí ---
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null, // Imagen decorativa
            modifier = Modifier.fillMaxSize(),
            // Escala la imagen para que llene la pantalla sin deformarse
            contentScale = ContentScale.Crop
        )
        Image(
            // Asegúrate de que 'R.drawable.logo2' es el nombre correcto de tu logo
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = "Logo de la App",
            modifier = Modifier
                .align(Alignment.TopStart) // Lo alinea arriba a la izquierda
                .padding(20.dp)           // Le da un poco de espacio
                .size(100.dp)              // Ajusta el tamaño como prefieras
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bienvenido, $nombre $apellido!",
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

            // Boton Gestionar Usuarios (solo para admin)
            if (isAdmin) {
                Button(
                    onClick = {
                        navController.navigate("userList")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    // --- AÑADE ESTO PARA CAMBIAR EL COLOR ---
                    border = BorderStroke(1.dp, Color(0xFF00C4FE)),
                    colors = ButtonDefaults.buttonColors(
                        // Aquí puedes poner el color que quieras para el fondo
                        containerColor = Color(0xFFA8FD3E),
                                contentColor = Color.Black,
                        disabledContentColor = Color.Black
                    )
                    // ------------------------------------------
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
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color(0xFF00C4FE)),
                colors = ButtonDefaults.buttonColors(
                    // Aquí puedes poner el color que quieras para el fondo
                    containerColor = Color(0xFFA8FD3E),
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black
                )

            ) {
                Text("Cerrar sesion")
            }
        }
    }
}
