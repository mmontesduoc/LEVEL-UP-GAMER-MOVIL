package com.example.laboratorio1

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.laboratorio1.data.AppDatabase
import com.example.laboratorio1.data.UserEntity
import com.example.laboratorio1.data.UserRepository
import com.example.laboratorio1.ui.theme.Laboratorio1Theme
import com.example.laboratorio1.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio1Theme {
                val preferencesManager = remember { PreferencesManager(applicationContext) }
                val database = remember { AppDatabase.getDatabase(applicationContext) }
                val userRepository = remember { UserRepository(database.userDao()) }

                // Insertar usuarios de prueba solo la primera vez
                LaunchedEffect(Unit) {
                    CoroutineScope(Dispatchers.IO).launch {
                        // Verificar si ya se inicializó la base de datos
                        if (!preferencesManager.isDatabaseInitialized()) {
                            // Usuarios ADMIN
                            userRepository.insertUser(
                                UserEntity(
                                    nombre = "Marco",
                                    apellido = "Montes",
                                    rut = "11.111.111-1",
                                    correo = "marco.montes@admin.cl",
                                    contrasena = "admin123",
                                    isAdmin = true
                                )
                            )
                            userRepository.insertUser(
                                UserEntity(
                                    nombre = "Marcos",
                                    apellido = "Hidalgo",
                                    rut = "22.222.222-2",
                                    correo = "marcos.hidalgo@admin.cl",
                                    contrasena = "admin123",
                                    isAdmin = true
                                )
                            )

                            // Usuarios NORMALES
                            userRepository.insertUser(
                                UserEntity(
                                    nombre = "Ana",
                                    apellido = "García",
                                    rut = "33.333.333-3",
                                    correo = "ana.garcia@user.cl",
                                    contrasena = "user123",
                                    isAdmin = false
                                )
                            )
                            userRepository.insertUser(
                                UserEntity(
                                    nombre = "Luis",
                                    apellido = "Pérez",
                                    rut = "44.444.444-4",
                                    correo = "luis.perez@user.cl",
                                    contrasena = "user123",
                                    isAdmin = false
                                )
                            )

                            // Marcar como inicializado
                            preferencesManager.markDatabaseAsInitialized()
                        }
                    }
                }

                AppNavigator(preferencesManager, userRepository)
            }
        }
    }
}

@Composable
fun AppNavigator(
    preferencesManager: PreferencesManager,
    userRepository: UserRepository
) {
    val navController = rememberNavController()
    val userViewModel = remember { com.example.laboratorio1.viewModel.UserViewModel(userRepository) }
    val isLoggedIn by preferencesManager.isLoggedInFlow.collectAsState(initial = false)
    val (nombre, apellido) = preferencesManager.getUserNameFlow.collectAsState(initial = "" to "").value

    val startDestination = if (isLoggedIn && nombre.isNotEmpty() && apellido.isNotEmpty()) {
        "home/${nombre}/${apellido}"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = "login") {
            LoginForm(navController, preferencesManager, userRepository)
        }
        composable(
            route = "home/{nombre}/{apellido}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("apellido") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombreArg = backStackEntry.arguments?.getString("nombre") ?: ""
            val apellidoArg = backStackEntry.arguments?.getString("apellido") ?: ""
            homeScreen(
                nombre = nombreArg,
                apellido = apellidoArg,
                navController = navController,
                preferencesManager = preferencesManager
            )
        }
        composable(route = "userList") {
            UserListScreen(navController, userViewModel)
        }
        composable(
            route = "addEditUser/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            AddEditUserScreen(navController, userViewModel, userId)
        }
    }
}

@Composable
fun LoginForm(
    navController: NavController,
    preferencesManager: PreferencesManager,
    userRepository: UserRepository,modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6

    // 1. Contenedor principal Box para apilar elementos
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 2. Imagen de fondo
        Image(
            // --- USA EL NOMBRE DE TU IMAGEN AQUÍ ---
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null, // Es decorativa, no necesita descripción
            modifier = Modifier.fillMaxSize(),
            // Escala la imagen para que llene la pantalla, recortando si es necesario
            contentScale = ContentScale.Crop
        )

        // 3. Columna con el contenido del formulario
        // (Esta es la columna que ya tenías, ahora dentro del Box)
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

            // --- Campo de texto para Email ---
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (it.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                        "Correo ingresado inválido"
                    } else { "" }
                    loginError = false
                },
                label = { Text("Email") },
                isError = emailError.isNotEmpty() || loginError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(29.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF39FF14),
                    unfocusedIndicatorColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Campo de texto para Contraseña ---
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (it.isNotEmpty() && it.length < 6) {
                        "La contraseña debe tener al menos 6 caracteres"
                    } else { "" }
                    loginError = false
                },
                label = { Text("Contraseña") },
                isError = passwordError.isNotEmpty() || loginError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(29.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF39FF14),
                    unfocusedIndicatorColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Mensaje de error general ---
            if (loginError) {
                Text(
                    text = "Usuario o contraseña incorrecta",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // --- Botón de Login ---
            // --- Botón de Login ---
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = userRepository.login(email, password)
                        if (user != null) {
                            preferencesManager.saveLoginState(true)
                            preferencesManager.saveUserName(user.nombre, user.apellido)
                            preferencesManager.saveAdminStatus(user.isAdmin)
                            launch(Dispatchers.Main) {
                                navController.navigate("home/${user.nombre}/${user.apellido}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                loginError = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEmailValid && isPasswordValid && email.isNotEmpty() && password.isNotEmpty(),
                // --- AÑADE ESTO PARA LOS COLORES DEL BOTÓN ---
                colors = ButtonDefaults.buttonColors(
                    // Color de fondo cuando el botón está HABILITADO
                    containerColor = Color(0xFFA8FD3E),
                    // Color de fondo cuando el botón está DESHABILITADO
                    disabledContainerColor = Color(0xFF00C4FE),
                    // Color del texto para ambos estados para asegurar la visibilidad
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black
                )
                // ---------------------------------------------
            ) {
                Text("Iniciar Sesión")
            }
        }
    }
}
