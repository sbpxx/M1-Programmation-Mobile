package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAF)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    public fun loginButton(view: View){
        val intentLogin = Intent(this,LoginActivity::class.java)
        startActivity(intentLogin)
        finish()
    }

    public fun registerButton(view: View){
        val intentRegister = Intent(this,RegisterActivity::class.java)
        startActivity(intentRegister)
        finish()
    }
}