package com.example.laboratorio1.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio1.model.UserDTO
import com.example.laboratorio1.network.RetrofitClient // Importante
import com.example.laboratorio1.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val userDto = userRepository.login(email, password)

                if (userDto != null) {
                    // 1. SI EL BACKEND ENVÍA EL TOKEN, LO GUARDAMOS
                    // Asumimos que tu UserDTO tiene un campo 'token' o que el backend lo inyectó ahí.
                    if (!userDto.token.isNullOrEmpty()) {
                        RetrofitClient.setToken(userDto.token)
                    }

                    // ÉXITO: Pasamos el DTO directo
                    _loginState.value = LoginState.Success(userDto)
                } else {
                    _loginState.value = LoginState.Error("Credenciales inválidas")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

// ESTADOS DE LA UI
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserDTO) : LoginState()
    data class Error(val message: String) : LoginState()
}
