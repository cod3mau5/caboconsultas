package com.example.caboconsultas.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.model.Appointment
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.activity_appointments.*
import retrofit2.Call
import retrofit2.Response

class AppointmentsActivity : AppCompatActivity() {
    private  val apiService: ApiService by lazy {
        ApiService.create()
    }
    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }
    private  val appointmentAdapter= AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        loadAppointments()

        val appointments= ArrayList<Appointment>()
//        appointments.add(Appointment(1,"Juan Ramos","2023-02-18","4:00 PM"))
//        appointments.add(Appointment(2,"Pepe Ramos","2023-02-16","9:00 PM"))
//        appointments.add(Appointment(3,"Miguel Ramos","2023-02-15","1:00 PM"))

        rvAppointments.layoutManager= LinearLayoutManager(this)
        rvAppointments.adapter= appointmentAdapter
    }

    private fun loadAppointments() {
        val token=preferences["token",""]
        val call= apiService.getAppointments("Bearer $token")
        call.enqueue(object: retrofit2.Callback<ArrayList<Appointment>>{
            override fun onResponse(
                call: Call<ArrayList<Appointment>>,
                response: Response<ArrayList<Appointment>>
            ) {
                if(response.isSuccessful){
                    response.body()?.let {
                        appointmentAdapter.appointments= it
                        appointmentAdapter.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })
    }
}