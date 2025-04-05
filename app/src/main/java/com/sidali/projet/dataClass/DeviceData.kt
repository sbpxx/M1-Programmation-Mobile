package com.sidali.projet.dataClass

data class DeviceData(
val id:String,
    val type:String,
    val availableCommands: ArrayList<String>,
    val opening : Float?,
    val power : Int?
)
