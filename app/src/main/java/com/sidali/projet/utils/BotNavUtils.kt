package com.sidali.projet.utils

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sidali.projet.R
import com.sidali.projet.activity.InviteActivity
import com.sidali.projet.activity.HousesActivity
import com.sidali.projet.activity.RemoteActivity

// Fonction pour configurer la barre de navigation inférieure
fun Activity.setupBottomNavUtils(houseId: String, token: String) {
    val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNav.itemIconTintList = null

    bottomNav.setOnItemSelectedListener { menuItem ->
        handleBottomNavItemClick(menuItem.itemId, houseId, token)
        true
    }

    updateSelectedNavItem(bottomNav)
}

private fun Activity.handleBottomNavItemClick(selectedItemId: Int, houseId: String, token: String) {
    when (selectedItemId) {
        R.id.navigation_devices -> {
            if (!isCurrentActivity(RemoteActivity::class.java)) {
                launchActivity(RemoteActivity::class.java, houseId, token)
            }
        }

        R.id.navigation_home -> {
            if (!isCurrentActivity(HousesActivity::class.java)) {
                launchActivity(HousesActivity::class.java, houseId, token)
            }
        }

        R.id.navigation_profile -> {
            if (!isCurrentActivity(InviteActivity::class.java)) {
                launchActivity(InviteActivity::class.java, houseId, token)
            }
        }
    }
}

private fun Activity.isCurrentActivity(activityClass: Class<*>): Boolean {
    return this::class.java.simpleName == activityClass.simpleName
}

private fun Activity.launchActivity(activityClass: Class<*>, houseId: String?, token: String) {
    val intent = Intent(this, activityClass).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    intent.putExtra("houseId", houseId)
    startActivity(intent)
}

fun Activity.updateSelectedNavItem(bottomNav: BottomNavigationView) {
    bottomNav.selectedItemId = when (this) {
        is HousesActivity -> R.id.navigation_home
        is RemoteActivity -> R.id.navigation_devices
        is InviteActivity -> R.id.navigation_profile
        else -> bottomNav.selectedItemId
    }
}
