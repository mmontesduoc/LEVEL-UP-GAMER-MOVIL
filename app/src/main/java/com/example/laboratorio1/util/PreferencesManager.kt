package com.example.laboratorio1.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _isLoggedInFlow = MutableStateFlow(isLoggedIn())
    val isLoggedInFlow: Flow<Boolean> = _isLoggedInFlow.asStateFlow()

    private val _isAdminFlow = MutableStateFlow(isAdmin())
    val isAdminFlow: Flow<Boolean> = _isAdminFlow.asStateFlow()

    private val _getUserNameFlow = MutableStateFlow(getUserName())
    val getUserNameFlow: Flow<Pair<String, String>> = _getUserNameFlow.asStateFlow()

    fun saveLoginState(loggedIn: Boolean) {
        prefs.edit().putBoolean("isLoggedIn", loggedIn).apply()
        _isLoggedInFlow.value = loggedIn
    }

    fun saveAdminStatus(admin: Boolean) {
        prefs.edit().putBoolean("isAdmin", admin).apply()
        _isAdminFlow.value = admin
    }

    fun saveUserName(nombre: String, apellido: String) {
        prefs.edit().putString("nombre", nombre).putString("apellido", apellido).apply()
        _getUserNameFlow.value = nombre to apellido
    }

    fun setLoggedIn(value: Boolean) {
        saveLoginState(value)
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun isAdmin(): Boolean = prefs.getBoolean("isAdmin", false)

    fun getUserName(): Pair<String, String> =
        (prefs.getString("nombre", "") ?: "") to (prefs.getString("apellido", "") ?: "")

    // --- AGREGADO: Función para borrar todos los datos (Cerrar sesión) ---
    fun clearData() {
        // 1. Borrar todo del almacenamiento físico
        prefs.edit().clear().apply()

        // 2. Resetear los flujos en memoria para que la UI se entere inmediatamente
        _isLoggedInFlow.value = false
        _isAdminFlow.value = false
        _getUserNameFlow.value = "" to ""
    }
}
