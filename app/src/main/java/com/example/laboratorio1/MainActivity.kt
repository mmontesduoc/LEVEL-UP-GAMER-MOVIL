package com.example.laboratorio1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.laboratorio1.ui.theme.Laboratorio1Theme

data class User(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val correo: String,
    val contrasena: String
)

object Usuarios {
    // Datos de ejemplo
    val lista = listOf(
        User(nombre = "Marco", apellido = "Montes", rut = "11111111-1", correo = "ddddd@s-th.cl", contrasena = "1234"),
        User(nombre = "Ana", apellido = "García", rut = "22222222-2", correo = "ana.g@example.com", contrasena = "admin")
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio1Theme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Login") {
        composable(route = "Login") {
            LoginForm(navController)
        }
        composable(
            route = "Home/{nombre}/{apellido}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("apellido") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val apellido = backStackEntry.arguments?.getString("apellido") ?: ""
            homeScreen(nombre = nombre, apellido = apellido)
        }
    }
}

@Composable
fun LoginForm(navController: NavController, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Campo de texto para Email ---
        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false // Reinicia el error al escribir
                loginError = false
            },
            label = { Text("Email") },
            isError = emailError || loginError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Campo de texto para Contraseña ---
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false // Reinicia el error al escribir
                loginError = false
            },
            label = { Text("Contraseña") },
            isError = passwordError || loginError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Mensaje de error general ---
        if (loginError) {
            Text(
                text = "Email o contraseña incorrectos.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // --- Botón de Login ---
        Button(
            onClick = {
                // Lógica de validación y navegación
                val user = Usuarios.lista.find { it.correo == email && it.contrasena == password }
                if (user != null) {
                    // Si el usuario es válido, navega a la pantalla Home
                    navController.navigate("Home/${user.nombre}/${user.apellido}")
                } else {
                    // Si no, muestra el error
                    loginError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Composable
fun homeScreen(nombre: String, apellido: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "¡Bienvenido, $nombre $apellido!")
    }
}
