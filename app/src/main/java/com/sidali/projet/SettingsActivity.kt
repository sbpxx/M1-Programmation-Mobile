package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)



            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

            val checkLoadHome = findViewById<CheckBox>(R.id.checkLoadHome)
            val checkUsefulButtons = findViewById<CheckBox>(R.id.checkUsefulButtons)


            checkLoadHome.isChecked = prefs.getBoolean("loadHome", false)
            checkUsefulButtons.isChecked = prefs.getBoolean("usefulButtons", true)

            checkLoadHome.setOnCheckedChangeListener { buttonView, isChecked ->
                prefs.edit().putBoolean("loadHome", isChecked).apply()
            }

            checkUsefulButtons.setOnCheckedChangeListener { buttonView, isChecked ->
                prefs.edit().putBoolean("usefulButtons", isChecked).apply()
            }


        }



    public fun Disconnect(view: View){
        val intentLogin = Intent(this,FirstActivity::class.java)
        startActivity(intentLogin)
        finish()

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}