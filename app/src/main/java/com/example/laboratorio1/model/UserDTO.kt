package com.example.laboratorio1.model

import com.google.gson.annotations.SerializedName

data class UserDTO(
    // Campos del constructor (aquí es donde GSON inyecta los datos)
    @SerializedName("id")
    val idUsuario: Int = 0,

    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val direccion: String? = null,
    val telefono: String? = null,

    @SerializedName("fecha_registro")
    val fechaRegistro: String? = null,

    @SerializedName("is_active")
    val _isActiveInt: Int = 1,

    val username: String,

    @SerializedName("fecha_nacimiento")
    val fechaNacimiento: String? = null,

    // MOVIDO AQUÍ: El token debe estar en el constructor para recibirlo del JSON
    val token: String? = null
) {
    // Cuerpo de la clase (Lógica extra)

    // Propiedad calculada para convertir el 1/0 a true/false
    val isActive: Boolean
        get() = _isActiveInt == 1
}
