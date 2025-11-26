package com.example.laboratorio1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.laboratorio1.repository.UserRepository
import com.example.laboratorio1.ui.AddEditUserScreen
import com.example.laboratorio1.ui.LoginForm
import com.example.laboratorio1.ui.UserListScreen
import com.example.laboratorio1.ui.HomeScreen
import com.example.laboratorio1.ui.theme.Laboratorio1Theme
import com.example.laboratorio1.util.PreferencesManager
import com.example.laboratorio1.viewModel.LoginViewModel
import com.example.laboratorio1.viewModel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio1Theme {
                // Inicializar preferencias
                val preferencesManager = remember { PreferencesManager(applicationContext) }

                // Inicializar Repository (Modo Solo API)
                val userRepository = remember { UserRepository() }

                // Inicializar ViewModels
                val userViewModel = remember { UserViewModel(userRepository) }
                val loginViewModel = remember { LoginViewModel(userRepository) }

                // Iniciar navegación
                AppNavigator(preferencesManager, userViewModel, loginViewModel)
            }
        }
    }
}

@Composable
fun AppNavigator(
    preferencesManager: PreferencesManager,
    userViewModel: UserViewModel,
    loginViewModel: LoginViewModel
) {
    val navController = rememberNavController()

    // Observamos el estado del login desde DataStore
    val isLoggedIn by preferencesManager.isLoggedInFlow.collectAsState(initial = false)
    // Obtenemos nombre y apellido guardados
    val userData = preferencesManager.getUserNameFlow.collectAsState(initial = "" to "").value
    val nombre = userData.first
    val apellido = userData.second

    val startDestination = if (isLoggedIn && nombre.isNotEmpty()) {
        "home/${nombre}/${apellido}"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // Login
        composable("login") {
            LoginForm(navController, preferencesManager, loginViewModel)
        }

        // Home
        composable(
            route = "home/{nombre}/{apellido}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("apellido") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombreArg = backStackEntry.arguments?.getString("nombre") ?: ""
            val apellidoArg = backStackEntry.arguments?.getString("apellido") ?: ""

            HomeScreen(nombreArg, apellidoArg, navController, preferencesManager)
        }

        // Lista de usuarios
        composable("userList") {
            UserListScreen(navController, userViewModel)
        }

        // Agregar/Editar usuario
        composable(
            route = "addEditUser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            AddEditUserScreen(navController, userViewModel, userId)
        }
    }
}
