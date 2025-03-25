package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    public fun registerNewAccount(view: View)
    {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent)
    }

    public fun login(view:View){
        val login = findViewById<EditText>(R.id.txtLogin)
        val password = findViewById<EditText>(R.id.txtPassword)

        val log : LoginData = LoginData(login.text.toString(),password.text.toString())

        Api().post<LoginData,TokenData?>("https://polyhome.lesmoulinsdudev.com/api/users/auth",log,::loginSuccess)
    }

    private fun loginSuccess(responseCode: Int,token: TokenData?){
    if (responseCode == 200){
        val intentMenu = Intent(this,MenuActivity::class.java)
        if (token != null){
            intentMenu.putExtra("token",token.token)
        }
    startActivity(intentMenu)
    }

    }

}


