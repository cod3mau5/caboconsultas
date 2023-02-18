package com.example.caboconsultas

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.caboconsultas.model.Appointment
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments= ArrayList<Appointment>()
        appointments.add(Appointment(1,"Juan Ramos","2023-02-18","4:00 PM"))
        appointments.add(Appointment(2,"Pepe Ramos","2023-02-16","9:00 PM"))
        appointments.add(Appointment(3,"Miguel Ramos","2023-02-15","1:00 PM"))
        rvAppointments.layoutManager= LinearLayoutManager(this)
        rvAppointments.adapter=AppointmentAdapter(appointments)
    }
}