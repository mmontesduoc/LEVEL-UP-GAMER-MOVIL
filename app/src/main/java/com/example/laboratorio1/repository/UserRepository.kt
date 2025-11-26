package com.example.laboratorio1.repository

import com.example.laboratorio1.network.RetrofitClient
import com.example.laboratorio1.model.UserDTO
import retrofit2.Response

class UserRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(email: String, password: String): UserDTO? {
        val response: Response<UserDTO> = api.login(mapOf("email" to email, "password" to password))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun register(user: UserDTO): UserDTO? {
        val response: Response<UserDTO> = api.register(user)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getUsers(): List<UserDTO> {
        val response = api.getUsers()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun updateUser(id: Int, user: UserDTO): UserDTO? {return try {
        // Importante: asegurarse de que el ID coincida
        val response = api.updateUser(id, user)
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return try {
            val response = api.deleteUser(id)
            // Si el código es 200, 202 o 204, se borró correctamente
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
