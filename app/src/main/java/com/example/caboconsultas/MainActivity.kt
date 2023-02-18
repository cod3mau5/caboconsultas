package com.example.caboconsultas

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.caboconsultas.PreferenceHelper.get
import com.example.caboconsultas.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
//        val session= preferences.getBoolean("session",false)
        val preferences= PreferenceHelper.defaultPrefs(this)


        if (preferences["session",false])
            goToMenuActivity()

        btnLogin.setOnClickListener {
            goToMenuActivity()
        }
        goToRegister.setOnClickListener{
            Toast.makeText(this,getString(R.string.toast_completa_tus_datos),Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun goToMenuActivity(){
        // Validate

        createSessionPreference()
        val intent=Intent(this,MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createSessionPreference() {
//        PreferenceManager.getDefaultSharedPreferences()

        /*
        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor=preferences.edit()
        editor.putBoolean("session",true)
        editor.apply()
         */
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["session"]= true
    }

}
