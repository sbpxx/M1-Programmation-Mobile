package com.sidali.projet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api
import com.sidali.projet.R
import com.sidali.projet.dataClass.LoginData
import com.sidali.projet.utils.showApiErrorToast

// Classe pour la création de compte

class RegisterActivity : AppCompatActivity() {

    private val registerUrl = "https://polyhome.lesmoulinsdudev.com/api/users/register"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
    }

    // Fonction pour lancer l'activité de connexion

    fun loginAccount(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Fonction pour s'inscrire

    fun register(view: View) {
        val login = findViewById<EditText>(R.id.txtRegLogin)
        val password = findViewById<EditText>(R.id.txtRegPassword)

        val reg = LoginData(login.text.toString(), password.text.toString())
        Api().post(registerUrl, reg, ::registerSuccess)
    }

    // Fonction pour fermer l'activité et revenir à l'activité de connexion

    private fun registerSuccess(responseCode: Int) {
        val intent = Intent(this, LoginActivity::class.java)
        if (responseCode == 200) {
            startActivity(intent)
            finish()
        } else {
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }
}
