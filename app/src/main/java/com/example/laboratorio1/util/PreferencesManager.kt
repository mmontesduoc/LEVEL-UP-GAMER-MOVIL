package com.example.laboratorio1.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_LASTNAME = stringPreferencesKey("user_lastname")
        private val IS_ADMIN = booleanPreferencesKey("is_admin")
        private val DB_INITIALIZED = booleanPreferencesKey("db_initialized")
    }

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveUserName(nombre: String, apellido: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = nombre
            prefs[USER_LASTNAME] = apellido
        }
    }

    suspend fun saveAdminStatus(isAdmin: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_ADMIN] = isAdmin
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = isLoggedIn
        }
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[IS_LOGGED_IN] ?: false
        }

    val getUserNameFlow: Flow<Pair<String, String>> = context.dataStore.data
        .map { prefs ->
            val nombre = prefs[USER_NAME] ?: ""
            val apellido = prefs[USER_LASTNAME] ?: ""
            Pair(nombre, apellido)
        }

    val isAdminFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[IS_ADMIN] ?: false
        }

    suspend fun markDatabaseAsInitialized() {
        context.dataStore.edit { prefs ->
            prefs[DB_INITIALIZED] = true
        }
    }

    suspend fun isDatabaseInitialized(): Boolean {
        val prefs = context.dataStore.data.map { it[DB_INITIALIZED] ?: false }
        return prefs.first()
    }
}
