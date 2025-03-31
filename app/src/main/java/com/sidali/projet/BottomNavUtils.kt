package com.sidali.projet

import android.app.Activity
import android.content.Intent
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Activity.setupTopNavUtils(houseId: String?, token: String) {
    val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
    topAppBar.setOnMenuItemClickListener { menuItem ->
        handleTopAppBarItemClick(menuItem.itemId, houseId, token)
        true
    }
}
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
            if (!isCurrentActivity(MenuActivity::class.java)) {
                launchActivity(MenuActivity::class.java, houseId, token)
            }
        }

        R.id.navigation_profile -> {
            if (!isCurrentActivity(InviteActivity::class.java)) {
                launchActivity(InviteActivity::class.java, houseId, token)
            }
        }
    }
}

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

private fun Activity.isCurrentActivity(activityClass: Class<*>): Boolean {
    return this::class.java.simpleName == activityClass.simpleName
}

private fun Activity.launchActivity(activityClass: Class<*>, houseId: String?, token: String) {
    val intent = Intent(this, activityClass).apply {
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    }
    intent.putExtra("houseId", houseId)
    intent.putExtra("token", token)
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