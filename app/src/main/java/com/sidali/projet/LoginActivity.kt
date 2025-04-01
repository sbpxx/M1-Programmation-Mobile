package com.sidali.projet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class LoginActivity : AppCompatActivity() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var stayConnectedCheck: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        login = findViewById<EditText>(R.id.txtLogin)
        password = findViewById<EditText>(R.id.txtPassword)
        stayConnectedCheck = findViewById<CheckBox>(R.id.checkBox)

        loadUserInfo()
    }

        public fun registerNewAccount(view: View)
        {
            val intent = Intent(this, RegisterActivity::class.java);
            startActivity(intent)
            finish()
        }

        public fun login(view:View){
             val log = login.text.toString()
             val pass = password.text.toString()

            val LogInfo : LoginData = LoginData(log,pass)

            Api().post<LoginData,TokenData?>("https://polyhome.lesmoulinsdudev.com/api/users/auth",LogInfo,::loginSuccess)
        }

        private fun loginSuccess(responseCode: Int,token: TokenData?){
            if (responseCode == 200) {
                if (token != null) {

                    saveUserInfo(token.token)

                    val intentMenu = Intent(this, MenuActivity::class.java)
                    startActivity(intentMenu)
                    finish()
                }
            }
        }

        private fun saveUserInfo(token : String) {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("login", login.text.toString())
            editor.putString("token", token)
            if (stayConnectedCheck.isChecked) {
                editor.putString("password", password.text.toString())
                editor.putBoolean("stayConnected", true)
            }else{
                editor.remove("password")
                editor.putBoolean("stayConnected", false)
            }
            editor.apply()
        }

        private fun loadUserInfo() {
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val stayConnected = sharedPreferences.getBoolean("stayConnected", false)

            if (stayConnected) {
                login.setText(sharedPreferences.getString("login", ""))
                password.setText(sharedPreferences.getString("password", ""))
                stayConnectedCheck.isChecked = true
                login(View(this))
            }
        }

}


