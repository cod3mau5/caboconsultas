package com.example.caboconsultas.model

data class Appointment(
    val id: Int,
    val DoctorName: String,
    val scheduledDate: String,
    val scheduledTime: String
)
