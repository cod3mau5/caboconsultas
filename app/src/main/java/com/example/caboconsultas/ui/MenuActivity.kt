package com.example.caboconsultas.ui

//import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.model.User
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import com.example.caboconsultas.util.PreferenceHelper.set
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private val apiService by lazy {
        ApiService.create()
    }
    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this )
    }
    private val authHeader by lazy {
        val token= preferences["token",""]
        "Bearer $token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val storeToken=intent.getBooleanExtra("store_token",false)
        if (storeToken){
            storeToken()
        }

        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        btnProfile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        btnCreateAppointment.setOnClickListener{
            createAppointment(it)
        }
        btnMyAppointments.setOnClickListener{
            val intent = Intent(this, AppointmentsActivity::class.java)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener{
            performLogout()
        }
    }

    private fun createAppointment(view: View) {
        val call= apiService.getUserInfo(authHeader)

        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    val user= response.body()
                    val phone=user?.phone?.length ?: 0
                    if(phone >= 6){
                        val intent = Intent(this@MenuActivity, CreateAppointmentActivitiy::class.java)
                        startActivity(intent)
                    }else{
                        Snackbar.make(view,"Es necesario asociar primero un numero de telefono a tu cuenta.",Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })

    }

    private fun storeToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMService", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val deviceToken = task.result
            // Store token
            val call=apiService.storeToken(authHeader,deviceToken)
            call.enqueue(object: Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Log.d(Companion.TAG,"Token registrado correctamente.")
                    }else{
                        Log.d(Companion.TAG,"Hubo un problema al registrar el token.")
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    toast(t.localizedMessage)
                }

            })
            // Log
//            Log.d("FCMService", token)
        })
    }

    private fun performLogout() {
        val call=apiService.postLogout(authHeader)
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
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["token"]= ""
    }

    companion object {
        private const val TAG= "MenuActivity"
    }
}