package com.sidali.projet

import androidx.activity.contextaware.OnContextAvailableListener

data class DeviceData(
val id:String,
    val type:String,
    val availableCommands: ArrayList<String>,
    val opening : Int?,
    val power : Int?
)
