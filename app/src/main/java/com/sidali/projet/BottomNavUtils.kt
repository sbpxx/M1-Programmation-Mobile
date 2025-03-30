package com.sidali.projet

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Activity.setupBottomNavUtils() {
    val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNav.itemIconTintList = null

    bottomNav.setOnItemSelectedListener { menuItem ->

        handleBottomNavItemClick(menuItem.itemId)
        true
    }

    updateSelectedNavItem(bottomNav)
}

private fun Activity.handleBottomNavItemClick(selectedItemId: Int) {
    when (selectedItemId) {
        R.id.navigation_devices -> {
            if (!isCurrentActivity(RemoteActivity::class.java)) {
                launchActivity(RemoteActivity::class.java)
            }
        }

        R.id.navigation_home -> {
            if (!isCurrentActivity(MenuActivity::class.java)) {
                launchActivity(MenuActivity::class.java)
            }
        }

        R.id.navigation_profile -> {
            if (!isCurrentActivity(InviteActivity::class.java)) {
                launchActivity(InviteActivity::class.java)
            }
        }
    }
}

private fun Activity.isCurrentActivity(activityClass: Class<*>): Boolean {
    return this::class.java.simpleName == activityClass.simpleName
}

private fun Activity.launchActivity(activityClass: Class<*>) {
    val intent = Intent(this, activityClass).apply {
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    }
    startActivity(intent)
}

fun Activity.updateSelectedNavItem(bottomNav: BottomNavigationView) {

    bottomNav.selectedItemId = when (this) {
        is MenuActivity -> R.id.navigation_home
        is RemoteActivity -> R.id.navigation_devices
        is InviteActivity -> R.id.navigation_profile
        else -> bottomNav.selectedItemId
    }
}