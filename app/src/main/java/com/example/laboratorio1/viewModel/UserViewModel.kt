package com.example.laboratorio1.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorio1.data.UserEntity
import com.example.laboratorio1.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val allUsers = userRepository.getAllUsersFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    fun addUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                userRepository.insertUser(user)
                _operationState.value = OperationState.Success("Usuario agregado exitosamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al agregar usuario: ${e.message}")
            }
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                _operationState.value = OperationState.Success("Usuario actualizado exitosamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al actualizar usuario: ${e.message}")
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(user)
                _operationState.value = OperationState.Success("Usuario eliminado exitosamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al eliminar usuario: ${e.message}")
            }
        }
    }

    fun deleteUserById(userId: Int) {
        viewModelScope.launch {
            try {
                userRepository.deleteUserById(userId)
                _operationState.value = OperationState.Success("Usuario eliminado exitosamente")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Error al eliminar usuario: ${e.message}")
            }
        }
    }

    suspend fun getUserById(id: Int): UserEntity? {
        return userRepository.getUserById(id)
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}

sealed class OperationState {
    object Idle : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}
