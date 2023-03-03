package com.example.caboconsultas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.io.response.LoginResponse
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import com.example.caboconsultas.util.PreferenceHelper.set
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val apiService:ApiService by lazy {
        ApiService.create()
    }
    private val snackBar by lazy {
        Snackbar.make(mainLayout, R.string.presiona_back_denuevo,Snackbar.LENGTH_SHORT)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
//        val session= preferences.getBoolean("session",false)
        val preferences= PreferenceHelper.defaultPrefs(this)


        if (preferences["token", ""].contains("|"))
            goToMenuActivity()

        btnLogin.setOnClickListener {
            performLogin()
        }
        goToRegister.setOnClickListener{
            Toast.makeText(this,getString(R.string.toast_completa_tus_datos),Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email=etEmail.text.toString()
        val password= etPassword.text.toString()
        val call=apiService.postLogin(email,password)
        if(email.trim().isEmpty() || password.trim().isEmpty()){
            toast("Porfavor ingrese un correo y contraseña")
            return
        }
        call.enqueue(object:  retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse= response.body()
                    if (loginResponse==null){
                        toast("Hubo un fallo.")
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.token)
                        toast("Bienvenido ${loginResponse.user.name}!")
                        goToMenuActivity(true)
                    }
                    if(!loginResponse.success){
                        toast("las credenciales son incorrectas.")
                    }
                }else{
                    toast("las credenciales son incorrectas.")
//                    toast(response.body().toString())
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.localizedMessage?.let { toast(it) }
            }
        })
    }

    private fun goToMenuActivity(isUserInput: Boolean = false){
        // createSessionPreference()
        val intent=Intent(this, MenuActivity::class.java)
        if (isUserInput){
            intent.putExtra("store_token",true)
        }
        startActivity(intent)
        finish()
    }
    private fun createSessionPreference(token:String) {
//        PreferenceManager.getDefaultSharedPreferences()

        /*
        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor=preferences.edit()
        editor.putBoolean("session",true)
        editor.apply()
         */
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["token"]= token
    }
    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
}