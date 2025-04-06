package com.sidali.projet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sidali.projet.R

// Classe pour la gestion des paramètres

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

        // Gestion des changements de statut des cases à cocher

        // Si la case "Charger la maison" est cochée, on charge la maison lors de la connexion

        checkLoadHome.setOnCheckedChangeListener { _, isChecked ->
            prefs
                .edit()
                .putBoolean("loadHome", isChecked)
                .apply()
        }

        // Si la case "Boutons utiles" est cochée, on affiche les boutons utiles dans l'interface

        checkUsefulButtons.setOnCheckedChangeListener { _, isChecked ->
            prefs
                .edit()
                .putBoolean("usefulButtons", isChecked)
                .apply()
        }
    }

    // Fonction pour se déconnecter

    fun Disconnect(view: View) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intentLogin = Intent(this, MainActivity::class.java)
        startActivity(intentLogin)
        finish()
    }

}