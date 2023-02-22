package com.example.caboconsultas.model
/*
    "id": 111,
    "name": "Robert",
    "email": "robert@gmail.com",
    "address": "Lomas Altas",
    "phone": "6241640107",
    "cedula": null,
    "role": "patient"
* */
data class User(
        val id: Int,
        val name: String,
        val email: String,
        val address: String,
        val phone: String,
        val cedula: String?,
        val role: String
    )