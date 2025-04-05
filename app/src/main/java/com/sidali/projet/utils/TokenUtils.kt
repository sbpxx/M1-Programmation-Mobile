package com.sidali.projet.utils

import android.app.Activity
import android.content.Context.MODE_PRIVATE


fun Activity.getToken(): String {
    val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
    return sharedPreferences.getString("token", "").toString()
}