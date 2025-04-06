package com.sidali.projet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api
import com.sidali.projet.R
import com.sidali.projet.dataClass.LoginData
import com.sidali.projet.dataClass.TokenData
import com.sidali.projet.utils.showApiErrorToast

// Classe pour la connexion de l'utilisateur

class LoginActivity : AppCompatActivity() {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var stayConnectedCheck: CheckBox

    private val loginUrl = "https://polyhome.lesmoulinsdudev.com/api/users/auth"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.txtLogin)
        password = findViewById(R.id.txtPassword)
        stayConnectedCheck = findViewById(R.id.checkBox)

        loadUserInfo()
    }

    // Fonction pour lancer l'activité de création de compte

    fun registerNewAccount(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Fonction pour connecter l'utilisateur

    fun login(view: View) {
        val log = login.text.toString()
        val pass = password.text.toString()
        val logInfo = LoginData(log, pass)

        Api().post<LoginData, TokenData?>(loginUrl, logInfo, ::loginSuccess)
    }

    // Fonction pour sauvegarder les informations de connexion de l'utilisateur et lancer l'activité des maisons

    private fun loginSuccess(responseCode: Int, token: TokenData?) {
        if (responseCode == 200 && token != null) {
            saveUserInfo(token.token)
            val intentHouses = Intent(this, HousesActivity::class.java)
            startActivity(intentHouses)
            finish()
        } else {
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }

    // Fonction pour sauvegarder les informations de connexion de l'utilisateur

    private fun saveUserInfo(token: String) {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("login", login.text.toString())
        editor.putString("token", token)

        if (stayConnectedCheck.isChecked) {
            editor.putString("password", password.text.toString())
            editor.putBoolean("stayConnected", true)
        } else {
            editor.remove("password")
            editor.putBoolean("stayConnected", false)
        }

        editor.apply()
    }

    // Fonction pour charger les informations de connexion de l'utilisateur

    private fun loadUserInfo() {
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val stayConnected = prefs.getBoolean("stayConnected", false)

        // Si l'utilisateur avait coché "Rester connecté", on charge les informations de connexion

        if (stayConnected) {
            login.setText(prefs.getString("login", ""))
            password.setText(prefs.getString("password", ""))
            stayConnectedCheck.isChecked = true
            login(View(this))
        }
    }
}
