package com.example.laboratorio1

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.laboratorio1.data.UserEntity
import com.example.laboratorio1.viewModel.OperationState
import com.example.laboratorio1.viewModel.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    userId: Int
) {
    val scope = rememberCoroutineScope()
    val isEditMode = userId > 0

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf("") }
    var apellidoError by remember { mutableStateOf("") }
    var rutError by remember { mutableStateOf("") }
    var correoError by remember { mutableStateOf("") }
    var contrasenaError by remember { mutableStateOf("") }

    val operationState by userViewModel.operationState.collectAsState()

    // Cargar datos del usuario si es modo edición
    LaunchedEffect(userId) {
        if (isEditMode) {
            scope.launch {
                val user = userViewModel.getUserById(userId)
                user?.let {
                    nombre = it.nombre
                    apellido = it.apellido
                    rut = it.rut
                    correo = it.correo
                    contrasena = it.contrasena
                    isAdmin = it.isAdmin
                }
            }
        }
    }

    // Observar estado de operación
    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationState.Success -> {
                navController.popBackStack()
                userViewModel.resetOperationState()
            }
            else -> {}
        }
    }

    // Validaciones
    val isNombreValid = nombre.isNotBlank()
    val isApellidoValid = apellido.isNotBlank()
    val isRutValid = rut.isNotBlank()
    val isCorreoValid = correo.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val isContrasenaValid = contrasena.length >= 6

    val isFormValid = isNombreValid && isApellidoValid && isRutValid && isCorreoValid && isContrasenaValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Usuario" else "Agregar Usuario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = if (it.isBlank()) "El nombre es requerido" else ""
                },
                label = { Text("Nombre") },
                isError = nombreError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (nombreError.isNotEmpty()) {
                Text(
                    text = nombreError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Campo Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = {
                    apellido = it
                    apellidoError = if (it.isBlank()) "El apellido es requerido" else ""
                },
                label = { Text("Apellido") },
                isError = apellidoError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (apellidoError.isNotEmpty()) {
                Text(
                    text = apellidoError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Campo RUT
            OutlinedTextField(
                value = rut,
                onValueChange = {
                    rut = it
                    rutError = if (it.isBlank()) "El RUT es requerido" else ""
                },
                label = { Text("RUT") },
                isError = rutError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("11111111-1") }
            )
            if (rutError.isNotEmpty()) {
                Text(
                    text = rutError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Campo Correo
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    correoError = if (it.isBlank()) {
                        "El correo es requerido"
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                        "Correo ingresado inválido"
                    } else {
                        ""
                    }
                },
                label = { Text("Correo electrónico") },
                isError = correoError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            if (correoError.isNotEmpty()) {
                Text(
                    text = correoError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Campo Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    contrasenaError = if (it.isBlank()) {
                        "La contraseña es requerida"
                    } else if (it.length < 6) {
                        "La contraseña debe tener al menos 6 caracteres"
                    } else {
                        ""
                    }
                },
                label = { Text("Contraseña") },
                isError = contrasenaError.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (contrasenaError.isNotEmpty()) {
                Text(
                    text = contrasenaError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Checkbox de Admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAdmin,
                    onCheckedChange = { isAdmin = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Usuario Administrador",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar
            Button(
                onClick = {
                    val user = UserEntity(
                        id = if (isEditMode) userId else 0,
                        nombre = nombre,
                        apellido = apellido,
                        rut = rut,
                        correo = correo,
                        contrasena = contrasena,
                        isAdmin = isAdmin
                    )

                    if (isEditMode) {
                        userViewModel.updateUser(user)
                    } else {
                        userViewModel.addUser(user)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(if (isEditMode) "Actualizar Usuario" else "Agregar Usuario")
            }

            // Mostrar error si hay
            if (operationState is OperationState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (operationState as OperationState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
