package com.example.laboratorio1.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.laboratorio1.R
import com.example.laboratorio1.model.UserDTO
import com.example.laboratorio1.viewModel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    userId: Int // 0 = Agregar, >0 = Editar
) {
    // 1. OBTENER DATOS PARA EDITAR
    // Observamos la lista para encontrar el usuario que coincida con el ID
    val users by userViewModel.allUsers.collectAsState()

    // Buscamos el usuario específico (si userId no es 0)
    val userToEdit = remember(userId, users) {
        if (userId != 0) users.find { it.idUsuario == userId } else null
    }

    // 2. INICIALIZAR CAMPOS
    // Usamos 'remember(userToEdit)' para que se actualicen cuando cargue el usuario
    var nombre by remember(userToEdit) { mutableStateOf(userToEdit?.nombre ?: "") }
    var apellido by remember(userToEdit) { mutableStateOf(userToEdit?.apellido ?: "") }
    var email by remember(userToEdit) { mutableStateOf(userToEdit?.email ?: "") }
    var username by remember(userToEdit) { mutableStateOf(userToEdit?.username ?: "") }

    // La contraseña la dejamos vacía por seguridad al editar, o la pides de nuevo
    var password by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (userId == 0) "Agregar Usuario" else "Editar Usuario",
                        color = Color.Black, // Cambia a White si usas fondo oscuro en la barra
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.fondo2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = apellido,
                            onValueChange = { apellido = it },
                            label = { Text("Apellido") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Contraseña: Opcional al editar
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(if(userId == 0) "Contraseña" else "Contraseña (Opcional)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (userId == 0 && (nombre.isBlank() || password.isBlank())) {
                                    errorMessage = "Completa los campos"
                                    return@Button
                                }
                                isLoading = true

                                coroutineScope.launch {
                                    // Si es editar y no escribieron pass, mantenemos la vieja (hack temporal)
                                    // Lo ideal es que el backend ignore el campo si viene vacío.
                                    val finalPassword = if (userId != 0 && password.isBlank()) {
                                        userToEdit?.password ?: ""
                                    } else {
                                        password
                                    }

                                    val user = UserDTO(
                                        idUsuario = userId,
                                        nombre = nombre,
                                        apellido = apellido,
                                        email = email,
                                        password = finalPassword,
                                        username = username,
                                        _isActiveInt = 1
                                    )

                                    val callback: (Boolean) -> Unit = { success ->
                                        isLoading = false
                                        if (success) {
                                            navController.popBackStack()
                                        } else {
                                            errorMessage = "Error al guardar"
                                        }
                                    }

                                    // 3. LOGICA DIFERENCIADA
                                    if (userId == 0) {
                                        userViewModel.register(user, callback)
                                    } else {
                                        userViewModel.updateUser(user, callback)
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF197278),
                                contentColor = Color.White
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(if (userId == 0) "GUARDAR USUARIO" else "ACTUALIZAR USUARIO")
                            }
                        }
                    }
                }
            }
        }
    }
}
