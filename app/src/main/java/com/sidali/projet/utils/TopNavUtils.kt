package com.sidali.projet.utils

import android.app.Activity
import android.content.Intent
import com.google.android.material.appbar.MaterialToolbar
import com.sidali.projet.R
import com.sidali.projet.activity.SettingsActivity

// houseId et token sont non utilisés pour l'instant dans les settings mais c'était en prédiction pour d'autres fonctionnalités

// Fonction pour configurer la barre de navigation supérieure

fun Activity.setupTopNavUtils(houseId: String?, token: String) {
    val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
    topAppBar.setOnMenuItemClickListener { menuItem ->
        handleTopAppBarItemClick(menuItem.itemId, houseId, token)
        true
    }
}

// Fonction pour gérer les clics sur les items de la barre de navigation supérieure

private fun Activity.handleTopAppBarItemClick(itemId: Int, houseId: String?, token: String) {
    when (itemId) {
        R.id.action_back -> {
            finish()
        }

        R.id.action_settings -> {
            launchActivity(SettingsActivity::class.java, houseId, token)
        }
    }
}

// Fonction pour lancer une nouvelle activité
private fun Activity.launchActivity(activityClass: Class<*>, houseId: String?, token: String) {
    val intent = Intent(this, activityClass).apply {
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    }
    intent.putExtra("houseId", houseId)
    startActivity(intent)
}
