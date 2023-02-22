package com.example.caboconsultas.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import com.example.caboconsultas.util.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private val apiService by lazy {
        ApiService.create()
    }
    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this )
    }
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
            performLogout()
        }
    }

    private fun performLogout() {
        val token= preferences["token",""]
        val call=apiService.postLogout("Bearer $token")
        call.enqueue(object: retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    clearSessionPreference()
                    val intent = Intent(this@MenuActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })
    }

    private fun clearSessionPreference() {
        /*
        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor=preferences.edit()
        editor.putBoolean("session",false)
        editor.apply()
         */
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["token"]= ""
    }
}