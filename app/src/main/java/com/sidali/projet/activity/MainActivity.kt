package com.sidali.projet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sidali.projet.R

class MainActivity : AppCompatActivity() {

    private lateinit var intentLogin: Intent
    private lateinit var intentRegister: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAF)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         intentLogin = Intent(this, LoginActivity::class.java)
         intentRegister = Intent(this, RegisterActivity::class.java)
        isAutoConnexion()
    }

    public fun loginButton(view: View){
        startActivity(intentLogin)
        finish()
    }

    public fun registerButton(view: View){
        startActivity(intentRegister)
        finish()
    }

    private fun isAutoConnexion(){

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val stayConnected = sharedPreferences.getBoolean("stayConnected", false)

        if (stayConnected) {
            startActivity(intentLogin)
            finish()
        }
    }
}