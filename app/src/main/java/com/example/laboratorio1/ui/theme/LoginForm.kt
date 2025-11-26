package com.example.laboratorio1.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.laboratorio1.R
import com.example.laboratorio1.util.PreferencesManager
import com.example.laboratorio1.viewModel.LoginState // Importante
import com.example.laboratorio1.viewModel.LoginViewModel // Importante: Usamos LoginViewModel

@Composable
fun LoginForm(
    navController: NavController,
    preferencesManager: PreferencesManager,
    loginViewModel: LoginViewModel, // CAMBIO 1: Usamos LoginViewModel
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // CAMBIO 2: Observamos el estado del Login (Idle, Loading, Success, Error)
    val loginState by loginViewModel.loginState.collectAsState()

    // CAMBIO 3: Reaccionamos a los cambios de estado (Navegación)
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                val user = state.user
                // Guardamos en preferencias
                preferencesManager.saveLoginState(true)

                // Usamos los datos que vienen de la API (UserDTO)
                // Nota: Usamos el operador elvis ?: "" por si vienen nulos
                preferencesManager.saveUserName(user.nombre ?: "", user.apellido ?: "")

                // Si tu UserDTO no tiene campo isAdmin/isActive, guardamos false por defecto
                preferencesManager.saveAdminStatus(false)

                // Navegar al Home
                navController.navigate("home/${user.nombre ?: "Usuario"}/${user.apellido ?: ""}") {
                    popUpTo("login") { inclusive = true }
                }
                // Reseteamos el estado para que no intente navegar de nuevo si volvemos
                loginViewModel.resetState()
            }
            is LoginState.Error -> {
                loginError = true
                errorMessage = state.message
            }
            else -> { /* Idle o Loading */ }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "Logo de la App",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; loginError = false },
                label = { Text("Email", color = Color.White) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; loginError = false },
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (loginError) {
                // Mostramos el mensaje de error que viene del ViewModel o uno genérico
                Text(
                    text = errorMessage.ifEmpty { "Usuario o contraseña incorrecta" },
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Mostrar indicador de carga si estamos esperando a la API
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = {
                        loginViewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp), // Le damos un poco más de altura para que se vea moderno
                    shape = RoundedCornerShape(12.dp), // Bordes redondeados
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA1FE44), // Tu color verde neón
                        contentColor = Color.Black,         // Texto negro para que contraste bien con el verde claro
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp // Efecto de hundirse al presionar
                    )
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold // Texto en negrita para resaltar
                    )
                }

            }
        }
    }
}
