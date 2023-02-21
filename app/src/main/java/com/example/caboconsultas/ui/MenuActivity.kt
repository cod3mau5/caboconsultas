package com.example.caboconsultas.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.caboconsultas.PreferenceHelper
import com.example.caboconsultas.PreferenceHelper.set
import com.example.caboconsultas.R
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        btnCreateAppointment.setOnClickListener{
            val intent = Intent(this, CreateAppointmentActivitiy::class.java)
            startActivity(intent)
        }
        btnMyAppointments.setOnClickListener{
            val intent = Intent(this, AppointmentsActivity::class.java)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener{
            clearSessionPreference()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun clearSessionPreference() {
        /*
        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor=preferences.edit()
        editor.putBoolean("session",false)
        editor.apply()
         */
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["session"]= false
    }
}