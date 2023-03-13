package com.example.caboconsultas.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.caboconsultas.R

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val name: String? = intent.getStringExtra("name")

    }
}