package com.example.laboratorio1.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.laboratorio1.R
import com.example.laboratorio1.util.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    nombre: String,
    apellido: String,
    navController: NavController,
    preferencesManager: PreferencesManager
) {
    val scope = rememberCoroutineScope()

    // Usamos Box para poder poner la imagen de fondo detrás de todo
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. IMAGEN DE FONDO (La misma del Login)
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Scaffold transparente para mantener la estructura
        Scaffold(
            containerColor = Color.Transparent
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Tarjeta contenedora para que el texto sea legible sobre el fondo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),

                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bienvenido,",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Text(
                            text = "$nombre $apellido",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botón para ir a la lista (Estilo verde neón como el login)
                        Button(
                            onClick = { navController.navigate("userList") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(30.dp), // <--- CAMBIO MANUAL 4 (Radio 30)
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF197278), // <--- CAMBIO MANUAL 5 (Azul #0e549b)
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Text(
                                text = "VER LISTA DE USUARIOS",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de Cerrar Sesión
                        Button(
                            onClick = {
                                scope.launch {
                                    preferencesManager.clearData()
                                    navController.navigate("login") {
                                        popUpTo("home/{nombre}/{apellido}") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(30.dp), // <--- CAMBIO MANUAL 6 (Radio 30)
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF197278), // <--- CAMBIO MANUAL 7 (Mismo Azul)
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Text(
                                text = "CERRAR SESIÓN",
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                }
            }
        }
    }
}
