package com.example.caboconsultas.io.response

import com.example.caboconsultas.model.User

data class LoginResponse(val success: Boolean, val user: User, val token: String)