package com.sidali.projet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sidali.projet.R

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

        checkLoadHome.setOnCheckedChangeListener { _, isChecked ->
            prefs
                .edit()
                .putBoolean("loadHome", isChecked)
                .apply()
        }

        checkUsefulButtons.setOnCheckedChangeListener { _, isChecked ->
            prefs
                .edit()
                .putBoolean("usefulButtons", isChecked)
                .apply()
        }
    }

    fun Disconnect(view: View) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intentLogin = Intent(this, MainActivity::class.java)
        startActivity(intentLogin)
        finish()
    }

}