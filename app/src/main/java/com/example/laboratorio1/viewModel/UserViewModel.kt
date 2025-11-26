package com.example.laboratorio1.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio1.model.UserDTO
import com.example.laboratorio1.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OperationState {
    object Idle: OperationState()
    data class Success(val message: String): OperationState()
    data class Error(val message: String): OperationState()
}

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<UserDTO?>(null)
    val user: StateFlow<UserDTO?> = _user

    private val _allUsers = MutableStateFlow<List<UserDTO>>(emptyList())
    val allUsers: StateFlow<List<UserDTO>> = _allUsers

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    init {
        fetchUsers()
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.login(email, password)
                _user.value = result
                onResult(result != null)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun register(user: UserDTO, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.register(user)
                if (result != null) {
                    fetchUsers()
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }



    // Función para EDITAR
    fun updateUser(user: UserDTO, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.updateUser(user.idUsuario, user)
                if (result != null) {
                    // TRUCO: Recargar lista inmediatamente
                    _allUsers.value = emptyList()
                    fetchUsers()
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }


    fun fetchUsers() {
        viewModelScope.launch {
            try {
                _allUsers.value = repository.getUsers()
            } catch (e: Exception) {
                _allUsers.value = emptyList()
            }
        }
    }

    fun deleteUser(user: UserDTO) {
        viewModelScope.launch {
            try {
                // Asegúrate que repository.deleteUser acepte (Int)
                val success = repository.deleteUser(user.idUsuario)
                if (success) {
                    _operationState.value = OperationState.Success("Usuario eliminado correctamente")
                    fetchUsers()
                } else {
                    _operationState.value = OperationState.Error("No se pudo eliminar el usuario")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error de conexión")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}
