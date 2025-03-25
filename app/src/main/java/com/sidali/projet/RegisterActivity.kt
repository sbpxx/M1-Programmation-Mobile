package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    public fun loginAccount(view: View)
    {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
    }

    public fun register(view:View){
        val login = findViewById<EditText>(R.id.txtRegLogin)
        val password = findViewById<EditText>(R.id.txtRegPassword)

        val reg : LoginData = LoginData(login.text.toString(),password.text.toString())

        Api().post<LoginData>("https://polyhome.lesmoulinsdudev.com/api/users/register",reg,::registerSuccess)
    }

    private fun registerSuccess(responseCode: Int){
        if (responseCode == 200){
            finish()
        }
    }
}