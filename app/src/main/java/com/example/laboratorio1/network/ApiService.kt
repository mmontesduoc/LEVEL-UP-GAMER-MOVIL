package com.example.laboratorio1.network

import com.example.laboratorio1.model.UserDTO
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body user: Map<String, String>): Response<UserDTO>

    @POST("auth/register")
    suspend fun register(@Body user: UserDTO): Response<UserDTO>

    @GET("auth/users") // Apunta al método que acabamos de crear en AuthController
    suspend fun getUsers(): Response<List<UserDTO>>

    @PUT("auth/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDTO): Response<UserDTO>

    @DELETE("auth/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Void>
    }



