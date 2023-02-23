package com.example.caboconsultas.model

import com.google.gson.annotations.SerializedName

//"id": 303,
//"description": "ayuda juaannn!!!",
//"scheduled_date": "2023-02-22",
//"type": "Consulta",
//"created_at": "2023-02-22T16:27:21.000000Z",
//"status": "confirmada",
//"scheduled_time_12": "9:00 AM",
//"specialty": {
//    "id": 19,
//    "name": "Ortopedia"
//},
//"doctor": {
//    "id": 2,
//    "name": "Juan"
//}
data class Appointment(
    val id: Int,
    val description: String,
    val type: String,
    val status:String,

    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("scheduled_time_12") val scheduledTime: String,
    @SerializedName("created_at") val CreatedAt: String,

    val specialty: Specialty,
    val doctor: Doctor
)
