package com.example.caboconsultas.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.io.response.LoginResponse
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {
    private val apiService by lazy {
        ApiService.create()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        tvGoToLogin.setOnClickListener {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btnRegister.setOnClickListener{
            performRegister()
        }
    }

    private fun performRegister() {
        val name= etRegisterName.text.toString().trim()
        val email= etRegisterEmail.text.toString().trim()
        val password= etRegisterPassword.text.toString()
        val passwordConfirmation= etRegisterPasswordConfirmation.text.toString()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()){
            toast("Porfavor completar todos los campos.")
            return
        }
        if (password != passwordConfirmation){
            toast("Las contraseñas no coinciden.")
            return
        }
        val call=apiService.postRegister(name,email,password,passwordConfirmation)
        call.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val loginResponse= response.body()
                    if (loginResponse==null){
                        toast("Hubo un fallo.")
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.token)
                        toast("Bienvenido ${loginResponse.user.name}!")
                        goToMenuActivity()
                    }
                    if(!loginResponse.success){
                        toast("las credenciales son incorrectas.")
                    }
                }else{
                    Log.d(TAG, "onResponse: ${response.message()}")
                    toast("Ocurrio un error en el registro")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }
        })
    }
    private fun createSessionPreference(token:String) {
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["token"]= token
    }
    private fun goToMenuActivity(){
        // Validate

//        createSessionPreference()
        val intent=Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}