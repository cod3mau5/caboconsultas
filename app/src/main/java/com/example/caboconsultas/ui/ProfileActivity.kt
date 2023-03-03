package com.example.caboconsultas.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.model.User
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.card_view_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private val apiService: ApiService by lazy{
        ApiService.create()
    }
    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val token=preferences["token",""]
        val authHeader="Bearer $token"
        val call= apiService.getUserInfo(authHeader)
        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful){
                    val user= response.body()
                    if(user != null)
                        displayProfileData(user)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                toast(t.localizedMessage)
            }
        })


    }

    private fun displayProfileData(user: User) {
        etName.setText(user.name)
        etPhone.setText(user.phone)
        etAddress.setText(user.address)
        progressBarProfile.visibility= View.GONE
        linearLayoutProfile.visibility=View.VISIBLE
        btnSaveProfile.setOnClickListener{
            saveProfile()

        }
    }

    private fun saveProfile() {

        val name= etName.text.toString()
        val phone= etPhone.text.toString()
        val address= etAddress.text.toString()

        if (name.length < 4 ){
            inputLayoutName.error="El nombre ingresado es demaciado corto."
            return
        }
        val token=preferences["token",""]
        val authHeader="Bearer $token"

        val call= apiService.postUserInfo(
            authHeader,
            name,
            phone,
            address
        )
        call.enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                toast("Los camobios se han guradado correctamente")
                finish()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

        })
    }
}